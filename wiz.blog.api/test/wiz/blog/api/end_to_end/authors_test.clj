(ns wiz.blog.api.end-to-end.authors-test
  (:require
   [clojure.test :as t :refer [deftest is use-fixtures]]
   [restpect.core :refer [created not-found ok]]
   [restpect.json :refer [DELETE GET POST PUT]]
   [wiz.blog.api.setup :refer [api fixture once-fixture]]))

(use-fixtures :each fixture)
(use-fixtures :once once-fixture)

(deftest create-author
  (ok
   (POST (api "/authors")
     {:name      "Li2"
      :email     "li2@test.com"
      :nickname  "L"
      :biography nil})
   {:id        integer?
    :name      "Li2"
    :email     "li2@test.com"
    :nickname  "L"
    :biography nil}))

(deftest get-author
  (ok
   (GET (api "/authors/1"))
   {:id        integer?
    :name      string?
    :email     string?
    :nickname  string?
    :biography #(or (string? %) (nil? %))}))

(deftest not-found-author
  (not-found
   (GET (api "/authors/100000"))
   {:message "not-found"}))

(deftest get-all-authors
  (ok
   (GET (api "/authors"))
   #{{:id        integer?
      :name      string?
      :email     string?
      :nickname  string?
      :biography #(or (string? %) (nil? %))}}))
