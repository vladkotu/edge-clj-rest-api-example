-- :name create-authors-table :!
CREATE TABLE IF NOT EXISTS authors (
  id serial PRIMARY KEY,
  name varchar,
  email varchar(120) UNIQUE,
  nickname varchar(80) NOT NULL,
  biography text,
  created_at timestamp NOT NULL default current_timestamp
)

-- :name drop-authors-table :!
DROP TABLE IF EXISTS authors

-- :name insert-author :! :n
INSERT INTO authors (name, email, nickname, biography)
  VALUES (:name,  :email, :nickname, :biography)

-- :name insert-authors :! :n
INSERT INTO authors (name, email, nickname, biography)
  VALUES :tuple*:authors

-- :name update-author-name :! :n
UPDATE authors set name = :name where id = :id

-- :name update-author :! :n
UPDATE authors set :fname = :fvalue where id = :id

-- :name generic-update :! :n
/* :require [clojure.string :as string]
    [hugsql.parameters :refer [identifier-param-quote]] */
update authors set
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
  ~*/
where id = :id

-- :name get-all-authors :? :*
SELECT * from authors order by created_at

-- :name get-author-by-email :? :1
SELECT * FROM authors WHERE email = :email

  -- :name get-author-by-id :? :1
SELECT * FROM authors WHERE id = :id

-- :name clj-expr-single :? :*
select
--~ (if (seq (:cols params)) ":i*:cols" "*")
from authors
--~ (if (get params :id) "where id = :id")
order by id
