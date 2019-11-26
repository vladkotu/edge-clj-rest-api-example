-- :name create-table :!
CREATE TABLE IF NOT EXISTS authors (
  id serial PRIMARY KEY,
  name varchar,
  email varchar(120) UNIQUE,
  nickname varchar(80) NOT NULL,
  biography text,
  created_at timestamp NOT NULL default current_timestamp
)

-- :name drop-table :!
DROP TABLE IF EXISTS authors

-- :name insert-distinct-entity :? :1
INSERT
INTO
authors (name, email, nickname, biography)
SELECT
  :name, :email, :nickname, :biography
WHERE
  NOT EXISTS(
    SELECT 1 FROM authors WHERE email = :email
  )
RETURNING id, name, email, nickname, biography, created_at

-- :name insert-entity :! :n
INSERT INTO authors (name, email, nickname, biography)
  VALUES (:name,  :email, :nickname, :biography)

-- :name insert-list-entities :! :n
INSERT INTO authors (name, email, nickname, biography)
  VALUES :tuple*:authors

-- :name update-author-name :! :n
UPDATE authors set name = :name where id = :id

-- :name update :! :n
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

-- :name select-all :? :*
SELECT id, name, nickname, email
FROM authors
--~ (if (:order params) "ORDER BY :i:order" "ORDER BY created_at")
--~ (when (:limit params) "LIMIT :limit")

-- :name select-by-email :? :1
SELECT * FROM authors WHERE email = :email

-- :name select-by-id :? :1
SELECT * FROM authors WHERE id = :id

-- :name delete-entity :! :n
DELETE from authors WHERE id = :id
