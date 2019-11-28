-- :name insert-entity :! :n
INSERT INTO books (title, author_id)
  VALUES (:title, :author-id)

-- :name insert-distinct-entity :? :1
INSERT
INTO
  books (title, author_id)
SELECT
  :title, :author-id
WHERE
  NOT EXISTS(
    SELECT 1 FROM books WHERE title = :title AND author_id = :author-id
  )
RETURNING id, title, author_id, created_at

-- :name insert-list-entities :! :n
INSERT INTO books (title, author_id)
  VALUES :tuple*:books

-- :name update-book-title :! :n
UPDATE books set title = :title where id = :id

-- :name update-entity :! :n
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
--~ (if (:order params) "ORDER BY :i:order" "ORDER BY created_at")
--~ (when (:limit params) "LIMIT :limit")


-- :name select-by-id :? :1
SELECT * FROM books WHERE id = :id

-- :name delete-entity :! :n
DELETE from books WHERE id = :id
