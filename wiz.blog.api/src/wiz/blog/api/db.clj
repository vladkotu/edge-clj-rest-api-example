(ns wiz.blog.api.db
  (:require
   [integrant.core :as ig]
   [clojure.tools.logging :as log]

   [wiz.blog.api.db.books :as books]
   [wiz.blog.api.db.authors :as authors]
   [wiz.blog.api.db.comments :as comments]))

(def db-conn (atom nil))

(defmethod ig/init-key ::db-spec
  [_ opts]
  (log/info "Initialize db-conn")
  (reset! db-conn opts))

(defmulti select :entity)

(defmethod select :books
  [_]
  (->> @db-conn
       books/select-all
       (sort-by :title)))

(defmethod select :book
  [{:keys [id]}]
  (authors/select-by-id @db-conn {:id id}))

(defmethod select :authors
  [_]
  (authors/select-all @db-conn))

(defmethod select :author
  [{:keys [id]}]
  (authors/select-by-id @db-conn {:id id}))

(defmethod select :comments
  [_]
  (comments/select-all @db-conn))

(defmethod select :comment
  [{:keys [id]}]
  (comments/select-by-id @db-conn {:id id}))
