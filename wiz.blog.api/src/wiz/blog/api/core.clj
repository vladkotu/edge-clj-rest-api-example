(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [yada.yada :as yada]
   [integrant.core :as ig]))

(defn string-resource
  [x]
  (yada/as-resource x))

(defmethod ig/init-key ::string
  [_ x]
  (string-resource x))
