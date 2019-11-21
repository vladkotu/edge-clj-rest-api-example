-- :name create-table :!
CREATE TABLE IF NOT EXISTS comments (
  id serial PRIMARY KEY,
  message varchar,
  author_id INT,
  book_id INT,
  created_at timestamp NOT NULL default current_timestamp
)

-- :name drop-table :!
DROP TABLE IF EXISTS comments

-- :name insert-entity :! :n
INSERT INTO comments (message, author_id, book_id)
VALUES (:message, :author-id, :book-id)

-- :name insert-list-entities :! :n
INSERT INTO comments (message, author_id, book_id)
VALUES :tuple*:comments

-- :name update-comment-message :! :n
UPDATE comments set message = :message where id = :id

-- :name update :! :n
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
SELECT id, message from comments order by created_at

-- :name select-by-id :? :1
SELECT * FROM comments WHERE id = :id
