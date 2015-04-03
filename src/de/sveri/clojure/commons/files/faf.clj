(ns de.sveri.clojure.commons.files.faf
  (:require [clojure.java.io :as io]))

(defn create-if-not-exists [folder]
  (when-not (.exists (io/file folder))
    (.mkdirs (io/file folder))))
