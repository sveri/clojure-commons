(ns de.sveri.clojure.commons.files.edn
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import (java.io PushbackReader)))

(defn from-edn [fname]
  "reads an edn file from classpath"
  (with-open [rdr (-> (io/resource fname)
                      io/reader
                      PushbackReader.)]
    (edn/read rdr)))

(defn filepath->edn [fp]
  "Reads a file from the given path and returns it's edn data"
  (-> fp
      (io/file)
      (io/reader)
      (PushbackReader.)
      (edn/read)))


;(ns de.sveri.clojure.commons.files.edn
;  (:import (java.io PushbackReader))
;  (:require [clojure.java.io :as io]
;            [clojure.edn :as edn]
;            ))

;(defn from-edn
;  "reads an edn file from classpath"
;  [fname]
;  (read-string (slurp (io/resource fname))))

;(defn from-edn [fname]
;  "reads an edn file from classpath"
;  (with-open [rdr (-> (io/resource fname)
;                      io/reader
;                      java.io.PushbackReader.)]
;    (edn/read rdr)))
