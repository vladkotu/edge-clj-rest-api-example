(ns wiz.blog.api.notify
  (:require
   [wiz.blog.api.add-print :as add-print]))


(defn before-print [& _]
  (println ::before)
  (add-print/ppp))
