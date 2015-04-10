(ns de.sveri.clojure.commons.files.faf
  (:require [clojure.java.io :as io]
            [clojure.core.typed :as t]
            [de.sveri.ctanns.clojure-core])
  (:import (java.io File)))

(t/ann create-if-not-exists [(t/U File String) -> boolean])
(defn create-if-not-exists [folder]
  (or (.exists (io/file folder)) (.mkdirs (io/file folder))))
