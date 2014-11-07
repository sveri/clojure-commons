(ns de.sveri.clojure.commons.tests.util
  (:require [datomic.api :as d]
    [clojure.test.check.generators :as gen]))

(def conn (atom nil))
(defn get-db-val [conn] (d/db conn))

(def db-mem-uri "datomic:mem:test-datom")

(defn setup-datomic [schema-path fixtures-path & [db-uri-param]]
  (let [db-uri (or db-uri-param db-mem-uri)]
    (d/create-database db-uri)
   (reset! conn (d/connect db-uri)))
  @(d/transact @conn (read-string (slurp schema-path)))
  @(d/transact @conn (read-string (slurp fixtures-path))))

(defn wrap-setup
  [setup teardown f]
  (setup)
  (f)
  (teardown))

(defn teardown-datomic [ & [db-uri]] (d/delete-database (or db-uri db-mem-uri)))

(def domain (gen/elements ["gmail.com" "hotmail.com" "computer.org"]))

(def email-gen
  ;"Generates email adresses with the domain from domain"
  (gen/fmap (fn [[name domain-name]]
              (str name "@" domain-name))
            (gen/tuple (gen/not-empty gen/string-alpha-numeric) domain)))