(ns wiz.blog.api.db.comments
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "wiz/blog/api/db/sql/comments.sql")
;; this should be removed in prod mode
(hugsql/def-sqlvec-fns "wiz/blog/api/db/sql/comments.sql")

(comment (def db (atom {:dbtype "postgresql", :user "vladkotu", :password "pwd123", :dbname "blog_db", :port 54320, :host "localhost"})))
(comment (let [body {:message "message from the repl" :author-id 2 :book-id 1}]
           (insert-entity @db body)))
(comment
  (let [query {:order "id"}]
    (select-all db query)
    #_(select-all-sqlvec query)))
(comment
  (create-table @db)
  (insert-list-entities
   @db
   {:comments [["love books about dbs and this one especially" 1 1]
               ["mee too" 2 1]
               ["lets learn db" 3 1]]})
  (insert-list-entities
   @db
   {:comments [["who is psyho here?" 3 2]
               ["i feel pain - fix it" 1 2]
               ["much better name - stop it" 3 2]]})
  (select-all @db))
