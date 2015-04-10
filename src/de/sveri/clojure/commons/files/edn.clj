(ns de.sveri.clojure.commons.files.edn
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.core.typed :refer [ann] :as t]
            [de.sveri.ctanns.clojure-core])
  (:import (java.io PushbackReader Reader BufferedReader InputStream File)
           (java.net URL URI Socket)))

(ann from-edn [String -> t/Any])
(defn from-edn [fname]
  "reads an edn file from classpath"
  (with-open [rdr (-> (io/resource fname)
                      io/reader
                      PushbackReader.)]
    (edn/read rdr)))

(ann filepath->edn [String -> t/Any])
(defn filepath->edn [fp]
  "Reads a file from the given path and returns it's edn data"
  (-> fp
      (io/file)
      (io/reader)
      (PushbackReader.)
      (edn/read)))
