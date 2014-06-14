(ns de.sveri.clojure.commons.tests.util
  (:require
    [clojure.test.check.generators :as gen]))

(def domain (gen/elements ["gmail.com" "hotmail.com" "computer.org"]))
(def email-gen
  "Generates email adresses with the domain from domain"
  (gen/fmap (fn [[name domain-name]]
              (str name "@" domain-name))
            (gen/tuple (gen/not-empty gen/string-alpha-numeric) domain)))