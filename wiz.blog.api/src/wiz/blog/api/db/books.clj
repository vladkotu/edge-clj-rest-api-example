(ns wiz.blog.api.db.books
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "wiz/blog/api/db/sql/books.sql")
;; this should be removed in prod mode
(hugsql/def-sqlvec-fns "wiz/blog/api/db/sql/books.sql")

(do
  (def db (atom {:dbtype "postgresql", :user "vladkotu", :password "pwd123", :dbname "blog_db", :port 54320, :host "localhost"})))
(do
  (let [query {:title "2 ololo" :author-id 3}]
    (insert-distinct-entity @db query)))

(comment
  (create-table @db)
  (insert-list-entities @db {:books [["Db Book of all Books" 1]
                                     ["Psyho Self control" 2]
                                     ["RDBMS coockbook" 1]]})
  (insert-list-entities @db {:books [["Event logigns in s UI systems" 3]
                                     ["Awareness" 4]]})
  (select-all @db))
