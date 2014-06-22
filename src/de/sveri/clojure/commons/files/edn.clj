(ns de.sveri.clojure.commons.files.edn
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn from-edn [fname]
  "reads an edn file from classpath"
  (with-open [rdr (-> (io/resource fname)
                      io/reader
                      java.io.PushbackReader.)]
    (edn/read rdr)))
