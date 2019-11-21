(ns wiz.blog.api.db
  (:require
   [integrant.core :as ig]
   [clojure.tools.logging :as log]

   [wiz.blog.api.db.books :as books]
   [wiz.blog.api.db.authors :as authors]))

(def db-conn (atom nil))

(defmethod ig/init-key ::db-spec
  [_ opts]
  (log/info "Initialize db-conn")
  (reset! db-conn opts))

(defmulti select :entity)

(defmethod select :books
  [{:keys [db-spec]}]
  (->> db-spec
       books/select-all
       (sort-by :title)))

(defmethod select :book
  [{:keys [id]}]
  (authors/select-by-id @db-conn id))

(defmethod select :authors
  [{:keys [db-spec]}]
  (authors/select-all db-spec))

(defmethod select :author
  [{:keys [db-spec]}]
  (authors/select-all db-spec))
