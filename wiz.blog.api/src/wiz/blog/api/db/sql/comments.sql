-- :name insert-entity :? :1
INSERT INTO comments (message, author_id, book_id)
VALUES (:message, :author-id, :book-id)
RETURNING id, message, author_id, book_id, created_at

-- :name insert-list-entities :! :n
INSERT INTO comments (message, author_id, book_id)
VALUES :tuple*:comments

-- :name update-comment-message :! :n
UPDATE comments set message = :message where id = :id

-- :name update-entity :! :n
/* :require [clojure.string :as string]
    [hugsql.parameters :refer [identifier-param-quote]] */
update comments set
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
~*/
where id = :id

-- :name select-all :? :*
SELECT id, message
FROM comments
--~ (when (:author params) "WHERE author_id=:author")
--~ (when (:book params) (if (:author params)  "AND book_id=:book"  "WHERE book_id=:book"))
--~ (if (:order params) "ORDER BY :i:order DESC" "ORDER BY created_at")
--~ (when (:limit params) "LIMIT :limit")

-- :name select-by-id :? :1
SELECT * FROM comments WHERE id = :id

-- :name delete-entity :! :n
DELETE from comments WHERE id = :id
