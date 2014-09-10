(ns de.sveri.clojure.commons.maps.util)

(defn remove-nil-keys [m] (into {} (filter second m)))
