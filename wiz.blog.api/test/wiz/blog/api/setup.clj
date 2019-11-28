(ns wiz.blog.api.setup
  (:require
   [wiz.blog.api.db :as db]
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "db/migrations/V1__Create_tables.sql")
(hugsql/def-db-fns "db/seed/V2__Insert_test_data.sql")
(hugsql/def-db-fns "wiz/blog/api/drop.sql")

(defn setup []
  (let [create (ns-resolve 'wiz.blog.api.setup (symbol 'create-tables))
        insert (ns-resolve 'wiz.blog.api.setup (symbol 'insert-data))]
    (create @db/db-conn)
    (insert @db/db-conn)))

(defn tear-down []
  (let [drop (ns-resolve 'wiz.blog.api.setup (symbol 'drop-tables))]
    (drop @db/db-conn)))

(defn once-fixture [f]
  (try
    (f)
    (catch Throwable _)
    (finally (setup))))

(defn fixture [f]
  (try
    (setup)
    (f)
    (catch Throwable _)
    (finally (tear-down))))

(def api-url "http://localhost:5757/v1")

(defn api [s-uri]
  (format (str "%s" s-uri) api-url))

