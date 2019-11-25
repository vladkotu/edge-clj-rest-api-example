-- :name create-table :!
CREATE TABLE IF NOT EXISTS books (
  id serial PRIMARY KEY,
  title varchar,
  author_id INT,
  created_at timestamp NOT NULL default current_timestamp
)

-- :name drop-table :!
DROP TABLE IF EXISTS books

-- :name insert-entity :! :n
INSERT INTO books (title, author_id)
  VALUES (:title, :author-id)

-- :name insert-list-entities :! :n
INSERT INTO books (title, author_id)
  VALUES :tuple*:books

-- :name update-book-title :! :n
UPDATE books set title = :title where id = :id

-- :name update :! :n
/* :require [clojure.string :as string]
    [hugsql.parameters :refer [identifier-param-quote]] */
update books set
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
  ~*/
where id = :id

-- :name select-all :? :*
SELECT id, title
FROM books
--~ (when (:author params) "WHERE author_id=:author")
--~ (if (:order params) "ORDER BY :order" "ORDER BY created_at")
--~ (when (:limit params) "LIMIT :limit")


-- :name select-by-id :? :1
SELECT * FROM books WHERE id = :id
