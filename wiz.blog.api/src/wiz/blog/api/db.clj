(ns wiz.blog.api.db
  (:require
   [integrant.core :as ig]
   [clojure.tools.logging :as log]
   [wiz.blog.api.db.authors :as authors]))

(def db (atom nil))

(defmethod ig/init-key ::db-spec
  [_ opts]
  (log/info "Initialize db")
  (reset! db opts))

