(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [yada.yada :as yada]
   [yada.swagger :as swagger]
   [wiz.blog.api.db :as db]
   [integrant.core :as ig]))

(defn pps [x] (with-out-str (clojure.pprint/pprint x)))
(defn pp [x] (clojure.pprint/pprint x))

(defn string-resource
  [x]
  (yada/as-resource x))

(defmethod ig/init-key ::root
  [_ x]
  (string-resource x))

(def def-opts {:produces   [{:media-type
                             #{"text/plain;q=0.9"
                               "application/edn;q=0.7"
                               "application/json;q=0.8"}}]
               :properties {:last-modified (new java.util.Date)}})

(defmethod ig/init-key ::item-blob
  [[_ id] {:keys [:wiz.blog.api.db/db-spec :resource/options]}]
  (yada/resource
   (merge
    def-opts
    options
    {:methods
     {:get
      {:response
       (fn [ctx]
         (let [data   (db/select {:entity (keyword (name id))
                                  :id (read-string (-> ctx :parameters :path :id))})
               result (if (nil? data)
                        (-> (:response ctx)
                            (assoc :status 404)
                            (assoc :body {:message "Not found"}))
                        data)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defmethod ig/init-key ::items-list
  [[_ id] {:keys [:wiz.blog.api.db/db-spec :resource/options]}]
  (yada/resource
   (merge
    def-opts
    options
    {:id id
     :methods
     {:get
      {:response
       (fn [ctx]
         (let [result (db/select {:entity (keyword (name id)) :db-spec db-spec})]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defmethod ig/init-key ::routes
  [_ {:keys [routes port basePath]}]
  ;; return pure routes for testing
  ;; routes
  (let [routes-content (first routes)]
    [["swagger.json"
      (yada/handler
       (swagger/swagger-spec-resource
        (swagger/swagger-spec
         routes-content
         {:info     {:title       "Books"
                     :version     "1.0"
                     :description "A simple application that demonstrates the use of multiple HTTP methods"}
          :host     (format "localhost:%d" port)
          :schemes  ["http"]
          :basePath basePath})))]
     routes-content]))
