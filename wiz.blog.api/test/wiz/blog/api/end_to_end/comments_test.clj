(ns wiz.blog.api.end-to-end.comments-test
  (:require [clojure.test :as t :refer [deftest is use-fixtures]]
            [restpect.core :refer [created not-found ok]]
            [restpect.json :refer [DELETE GET POST PUT]]
            [wiz.blog.api.setup :refer [fixture api]]))


(use-fixtures :each fixture)

(deftest create-comment
  (ok
   (POST (api "/comments")
         {:message   "chat on tests"
          :author-id 2
          :book-id   1})
   {:id        integer?
    :message   "chat on tests"
    :author_id 2
    :book_id   1}))

(deftest get-comment
  (ok
   (GET (api "/comments/1"))
   {:id        integer?
    :message   string?
    :author_id integer?
    :book_id   integer?}))

(deftest not-found-comment
  (not-found
   (GET (api "/comments/100000"))
   {:message "not-found"}))

(deftest get-all-comments
  (ok
   (GET (api "/comments"))
   #{{:id        integer?
      :message   string?}}))
