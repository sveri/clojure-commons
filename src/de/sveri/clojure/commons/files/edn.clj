(ns de.sveri.clojure.commons.files.edn
  (:import (java.io PushbackReader))
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn from-edn [fname]
  "reads an edn file from classpath"
  (read-string (slurp (clojure.java.io/resource fname))))
