(ns wiz.blog.api.feature2
  (:require
   [wiz.blog.api.feature1 :as feature1]))

(defn run [& _]
  (println :feature2-run)
  (feature1/foo))

