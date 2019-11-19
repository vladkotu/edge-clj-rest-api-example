(ns wiz.blog.api.db
  (:require
   [integrant.core :as ig]
   [wiz.blog.api.db.authors :as authors]))

(def db (atom nil))

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
  (authors/get-author-by-email @db {:email "svet@ttt.com"}))

(comment
  (authors/get-all-authors @db))

(defmethod ig/init-key ::db-spec
  [_ opts]
  (println opts)
  (reset! db opts))

