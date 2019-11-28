(ns revolt-migrations
  (:require
   [dev-extras :refer :all]
   [clojure.tools.logging :as log]
   [clojure.repl :refer [doc dir]]
   [clojure.tools.cli :as cli]
   [revolt.bootstrap]
   [revolt.task :as t]
   [revolt.migrations.task :as mt]))

(def ^:private ready-to-fly (atom nil))

(defn- pre-fly-check[]
  (when-not @ready-to-fly
    (reset! ready-to-fly (t/require-task ::mt/flyway))))

(defn- task [opts]
  (pre-fly-check)
  ((ns-resolve 'revolt-migrations (symbol @ready-to-fly)) opts))

(defn info
  "Flyway info: show statuses of all available migrations"
  ([] (info {}))
  ([opts] (task (merge opts {:action :info}))))

(defn migrate
  "Flyway migrate: show statuses of all available migrations"
  ([] (migrate {}))
  ([opts] (task (merge opts {:action :migrate}))))

(defn -main [& args]
  (log/info "-----   MY MAIN")
  (println "[Mig] " args))
