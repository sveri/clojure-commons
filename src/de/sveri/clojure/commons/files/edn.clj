(ns de.sveri.clojure.commons.files.edn
  ;(:import (java.io PushbackReader))
  (:require [clojure.java.io :as io]
            ;[clojure.edn :as edn]
            ))

(defn from-edn
  "reads an edn file from classpath"
  [fname]
  (read-string (slurp (io/resource fname))))
