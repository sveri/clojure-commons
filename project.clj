(defproject de.sveri/clojure-commons "0.1.3"
  :description "A clojure helper library for my personal projects."
  :url "https://github.com/sveri/clojure-commons"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/test.check "0.5.8"]]
  :deploy-repositories [["clojars-self" {:url           "https://clojars.org/repo"
                                         :sign-releases false}]]
  :source-paths ["src"])
