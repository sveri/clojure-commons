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
     attr
     search))

(defn find-all-from-column
  "Returns a list of entities. Expects a predefined query like this: `[:find ?e :where [?e ~username-kw]]"
  [db-val column-query]
  (d/q column-query db-val))

(defn get-by-id-lazy-ref
  "Returns a datom by just touching it, every reference inside it will be kept by id."
  [db-val id]
  (d/touch (d/entity db-val id)))

(defn retrieve-additional-datoms
  "Fetches the attribute identified by add-key
  from entity and puts it back into the map.
  Additionally the id of the entity is merged into the map"
  [db-val entity add-key]
  (let [entity-m (into {} entity)]
    (assoc
        (assoc entity-m add-key (map #(d/touch (d/entity db-val (:db/id %))) (seq (add-key entity-m))))
      :db/id (:db/id entity))))


; inserts
(defn get-or-create-entity
  "Retrieves the id of an entity defined by it's key through it's value.
  Entity has to be unique.
  It still works if entity is not unique, but expect unexpected behavior then."
  [db-conn part-id entity-key entity-val & [data-map]]
  {:pre [(and (keyword? entity-key)(keyword? part-id))]}
  (let [id (ffirst (find-by-attr-and-search-string (d/db db-conn) entity-key entity-val))]
    (or id
        (let [temp_id (d/tempid part-id)
              transact-data (merge {:db/id temp_id entity-key entity-val} data-map)
              tx @(d/transact db-conn [transact-data])]
          (d/resolve-tempid (d/db db-conn) (:tempids tx) temp_id)))))

(defn add-multi-ref-entity-to-entity
  "Function to add refs to an entity.
  Takes an additional data map for that ref and adds it to the new entity.
  If entity exists already it remains unchanged."
  [db-conn partition-id upsert-ref ref-key entity-key entity-val user-name & [data-map]]
  (let [ex-id (get-or-create-entity db-conn partition-id entity-key entity-val data-map)]
    @(d/transact db-conn [{:db/id [upsert-ref user-name] ref-key ex-id}])))

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

(defn update-entity-by-id [db-conn id data-map]
  @(d/transact db-conn [(merge {:db/id id } data-map)]))
