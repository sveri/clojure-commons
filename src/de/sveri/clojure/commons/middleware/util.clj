(ns de.sveri.clojure.commons.middleware.util
  (:require [clojure.string :as s]
            [clojure.walk :refer [prewalk]]))

(def trim-param-list [:params :form-params :edn-params])

(defn- trim-params [req p-list]
  (if (= :post (:request-method req))
    (let [prewalk-trim #(if (string? %) (s/trim %) %)]
      (reduce (fn [m k] (assoc m k (prewalk prewalk-trim (get-in req [k])))) req p-list))
    req))

(defn wrap-trimmings
  "string/trim every parameter in :params or :form-params"
  [handler]
  (fn [req] (handler (trim-params req trim-param-list))))
