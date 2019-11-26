(ns wiz.blog.api.db.authors
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "wiz/blog/api/db/sql/authors.sql")
;; this should be removed in prod mode
(hugsql/def-sqlvec-fns "wiz/blog/api/db/sql/authors.sql")

;; primers mostly expired as of names have been changed
(do (def db (atom {:dbtype "postgresql", :user "vladkotu", :password "pwd123", :dbname "blog_db", :port 54320, :host "localhost"})))
(do (let [body {:name "Li", :email "li@bisss.com", :nickname "L", :biography nil}]
           (insert-distinct-entity @db body)))

(comment (authors/insert-authors
          @db
          {:authors
           (map vals
                [{:name      "Ivan"
                  :email     "ivan@ttt.com"
                  :nickname  "ivanko"
                  :biography nil}
                 {:name      "Sven"
                  :email     "svet@ttt.com"
                  :nickname  "svenlo"
                  :biography nil}])}))

(comment
  (authors/get-author-by-email @db {:email "svet@ttt.com"})
  (let [{:keys [id]} (authors/get-author-by-email @db {:email "svet@ttt.com"})]
    (authors/generic-update @db {:id id :updates {:name "Svetlana"}})))

(comment
  (authors/clj-expr-single-sqlvec {:cols ["email" "name"]})
  (authors/clj-expr-single @db {:cols ["email" "name"] :id 1})
  (get-author-by-email @db {:email "svet@ttt.com"}))

(comment
  (authors/get-all-authors @db))

