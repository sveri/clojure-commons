(ns de.sveri.clojure.commons.log
  (:require [clojure.tools.logging :as log])
  (:import (java.io StringWriter PrintWriter)))


(defn error->string [e]
  (if (instance? Exception e)
    (let [sw (new StringWriter)
          pw (new PrintWriter sw)
          e-string (.printStackTrace e pw)]
      (.toString sw))
    e))



(defn trace [e]
  (log/trace (error->string e)))

(defn debug [e]
  (log/debug (error->string e)))

(defn info [e]
  (log/info (error->string e)))

(defn warn [e]
  (log/warn (error->string e)))

(defn error [e]
  (log/error (error->string e)))

(defn fatal [e]
  (log/fatal (error->string e)))
