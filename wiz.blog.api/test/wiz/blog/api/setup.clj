(ns wiz.blog.api.setup
  (:require
   [clojure.tools.logging :as log]
   [edge.system]
   [edge.test.system :refer [with-subsystem-fixture *system*]]
   [clojure.test :as t :refer [deftest is use-fixtures]]
   [hugsql.core :as hugsql]))

(hugsql/def-db-fns "db/migrations/V1__Create_tables.sql")
(hugsql/def-db-fns "db/seed/V2__Insert_test_data.sql")
(hugsql/def-db-fns "wiz/blog/api/drop.sql")

(def config (atom nil))

(defn setup []
  (if @config
    (let [create (ns-resolve 'wiz.blog.api.setup (symbol 'create-tables))
          insert (ns-resolve 'wiz.blog.api.setup (symbol 'insert-data))]
      (create (:db @config))
      (insert (:db @config)))
    (log/error "Impossible to setup db since to connection available")))

(defn tear-down []
  (let [drop (ns-resolve 'wiz.blog.api.setup (symbol 'drop-tables))]
    (drop (:db @config))))

(defn configure-system-once []
  (when-not @config
    ((with-subsystem-fixture [:wiz.blog.api.db/db-spec])
     #((reset! config {:db (get *system* :wiz.blog.api.db/db-spec)})
       (swap! config merge
              (:web (edge.system/config {:profile :test})
                    :ig/system))
       (log/info  :test-subsystem-inited  @config)))))

(defn once-fixture [f]
  (try
    (configure-system-once)
    (f)
    (catch Throwable e
      (log/error  :error-runing-fixture)
      (tear-down))
    (finally
      (setup)
      (log/debug  :finished "db filled with dev data"))))

(identity @config)

(defn fixture [f]
  (try
    (setup)
    (f)
    (catch Throwable _)
    (finally (tear-down))))

(defn api [s-uri]
  (when (map? @config)
    (str (:protocol @config)
         "://"
         (:host @config)
         (when (:port @config)
           (str ":" (:port @config)))
         "/v1" s-uri)))

(defn setup-fixtures []
  (use-fixtures :each fixture)
  (use-fixtures :once once-fixture))
