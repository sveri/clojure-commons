(ns de.sveri.clojure.commons.lists.util)

(defn filter-list
  "Searches for needle in a list of maps by keyword.
  (contains [[{:foo bar} {:foo foofoo}] foofoo :foo] will return {:foo foofoo}"
  [list needle keyword]
  (filter #(.contains (.toLowerCase (keyword %)) (.toLowerCase needle)) list))
