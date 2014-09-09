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

(defn find-all-from-column
  "Returns a list of entities. Expects a predefined query like this: `[:find ?e :where [?e ~username-kw]]"
  [db-val column-query]
  (d/q column-query db-val))

(defn get-by-id-lazy-ref
  "Returns a datom by just touching it, every reference inside it will be kept by id."
  [db-val id]
  (when id (d/touch (d/entity db-val id))))

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

(defn update-entity-by-id [db-conn id data-map]
  @(d/transact db-conn [(merge {:db/id id} data-map)]))
