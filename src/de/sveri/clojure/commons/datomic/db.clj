(ns de.sveri.clojure.commons.datomic.db
  (:require [datomic.api :as d]))


;reads
(defn find-entity-by-attr-and-search-string [db-val attr search-string]
  (d/entity db-val [attr search-string]))

(defn get-map-from-entity [entity]
  (d/touch entity))

(defn find-by-attr-and-search-string [db-val attr search]
  (d/q '[:find ?c
         :in $ ?attr ?name
         :where [?c ?attr ?name]]
       db-val
       (d/entid db-val attr)
       search))

(defn get-refs-from-entity
  "Searches all references from an entity.
  For instance, all events that belong to a user would translate to this:
  (d/q '[:find ?events
       :in $ ?email
       :where [?user :user/email ?email]
              [?events :event/user ?user]]
     db
     \"editor@example.com\")

     =>
  (get-refs-from-entity db-val :event/user :user/email \"editor@example.com\")"
  [db-val entity-key ref-key ref-name]
  (d/q '[:find ?ref
         :in $ ?ref-key-var ?ref-var ?entity-var
         :where [?user ?ref-key-var ?ref-var]
         [?ref ?entity-var ?user]]
       db-val
       ref-key
       ref-name
       entity-key))

(defn find-all-from-column
  "Returns a list of entities. Expects a predefined query like this: `[:find ?e :where [?e ~username-kw]]"
  [db-val column-query]
  (d/q column-query db-val))

(defn get-by-id-lazy-ref
  "Returns a datom by just touching it, every reference inside it will be kept by id."
  [db-val id]
  (when id (d/touch (d/entity db-val id))))

(defn touch-ref-for-entities
  "Realizes a reference from the entity in the entities list"
  [db-val ref-key entities]
  (let [get-by-id (fn [c] (into {} (get-by-id-lazy-ref db-val (get c :db/id))))
        update-vals (fn [value xs] (map #(update-in (into {} %) [value] get-by-id) xs))]
    (update-vals ref-key entities)))

(defn retrieve-additional-datoms
  "Fetches the attribute identified by add-key
  from entity and puts it back into the map.
  Additionally the id of the entity is merged into the map"
  [db-val entity-m add-key]
  (assoc entity-m add-key (mapv
                            #(when (get % :db/id)
                              (into {} (get-by-id-lazy-ref db-val (:db/id %))))
                            (seq (get entity-m add-key)))))

(defn get-db-id-from-uuid
  "Returns the :db/id that matches the given uuid. UUID must be of type java.util.UUID"
  [db-val uuid-kw uuid]
  (ffirst (find-by-attr-and-search-string db-val uuid-kw uuid)))

(defn get-uuid-from-db-id [db-val uuid-kw id]
  (uuid-kw (get-by-id-lazy-ref db-val id)))

; inserts
(defn create-entity
  "Returns a vector that can be inserted into datomic. Adds a temporary :db/id to the given data-map."
  [partition-id data-map]
  (let [temp_id (d/tempid partition-id)]
    [(merge {:db/id temp_id} data-map)]))

(defn insert-entity
  "Inserts an entity into the database. data-map has to contain a temporary generated id."
  [db-conn data-map]
  {:pre [(some? (:db/id (first data-map)))]}
  @(d/transact db-conn data-map))

(defn insert-data-map
  "Generates a temporary id and adds it to the data-map.
  Returns the database id generated for that new datom."
  [db-conn partition-id data-map]
  (let [temp_id (d/tempid partition-id)
        succ-tx (insert-entity db-conn [(merge {:db/id temp_id} data-map)])]
    (d/resolve-tempid (d/db db-conn) (:tempids succ-tx) temp_id)))


(defn update-entity-by-id [db-conn id data-map]
  @(d/transact db-conn [(merge {:db/id id} data-map)]))
