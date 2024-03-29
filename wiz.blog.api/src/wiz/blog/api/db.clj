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
  [{:keys [query]}]
  (log/info ::select.books " db method called" :query query)
  (books/select-all @db-conn query))

(defmethod select :book
  [{:keys [id]}]
  (log/info ::select.book " db method called" :id id)
  (books/select-by-id @db-conn {:id id}))

(defmethod select :authors
  [{:keys [query]}]
  (log/info ::select.authors " db method called" :query query)
  (authors/select-all @db-conn query))

(defmethod select :author
  [{:keys [id]}]
  (log/info ::select.author " db method called" :id id)
  (authors/select-by-id @db-conn {:id id}))

(defmethod select :comments
  [{:keys [query]}]
  (log/info ::select.comments " db method called" :query query)
  (comments/select-all @db-conn query))

(defmethod select :comment
  [{:keys [id]}]
  (log/info ::select.comment" db method called" :id id)
  (comments/select-by-id @db-conn {:id id}))

(defmulti insert :entity)

(defmethod insert :books
  [{:keys [body]}]
  (log/info ::insert.books " db method called" :body body)
  (books/insert-distinct-entity @db-conn body))

(defmethod insert :authors
  [{:keys [body]}]
  (log/info ::insert.authors " db method called" :body body)
  (authors/insert-distinct-entity @db-conn body))

(defmethod insert :comments
  [{:keys [body]}]
  (log/info ::insert.comments " db method called" :body body)
  (comments/insert-entity @db-conn body))

(defmulti delete :entity)

(defmethod delete :book
  [{:keys [id]}]
  (log/info ::delete.book " db method called" :id id)
  (books/delete-entity @db-conn {:id id}))

(defmethod delete :authors
  [{:keys [id]}]
  (log/info ::delete.authors " db method called" :id id)
  (authors/delete-entity @db-conn {:id id}))

(defmethod delete :comments
  [{:keys [id]}]
  (log/info ::delete.comments " db method called" :id id)
  (comments/delete-entity @db-conn {:id id}))
