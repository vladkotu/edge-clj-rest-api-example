(ns wiz.blog.api.db.authors
  (:require
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "wiz/blog/api/db/authors.sql")
(hugsql/def-sqlvec-fns "wiz/blog/api/db/authors.sql")

