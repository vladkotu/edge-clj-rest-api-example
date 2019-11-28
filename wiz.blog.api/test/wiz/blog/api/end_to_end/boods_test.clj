(ns wiz.blog.api.end-to-end.boods-test
  (:require [clojure.test :as t :refer [deftest is use-fixtures]]
            [restpect.core :refer [created not-found ok]]
            [restpect.json :refer [DELETE GET POST PUT]]
            [wiz.blog.api.setup :refer [fixture api]]))

(use-fixtures :each fixture)

(deftest create-book
  (ok
   (POST (api "/books")
         {:title     "new test book v4"
          :author-id 1})
   {:id integer?
    :title "new test book v4"
    :author_id 1}))

(deftest get-book
  (ok
   (GET (api "/books/1"))
   {:id        integer?
    :title     string?
    :author_id integer?}))

(deftest not-found-book
  (not-found
   (GET (api "/books/100000"))
   {:message "not-found"}))

(deftest get-all-books
  (ok
   (GET (api "/books"))
   #{{:id integer?
      :title string?}}))
