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

(defmethod ig/init-key ::fcom
  [_ xs]
  (string-resource (clojure.string/join "/" xs)))

(defmethod ig/init-key ::fcom-wrapped
  [_ {:keys [origin wrap-with]}]
  (let [f          (first origin)
        l          (last origin)
        wf         (first wrap-with)
        wl         (last wrap-with)
        sub-origin (subvec origin 1 (dec (count origin)))]
    (conj (apply vector (str wf f) sub-origin) (str l wl))))


