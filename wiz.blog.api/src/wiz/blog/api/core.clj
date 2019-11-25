(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [schema.core :as schema]
   [yada.yada :as yada]
   [yada.swagger :as swagger]
   [wiz.blog.api.db :as db]
   [integrant.core :as ig]))

;; (set! *print-namespace-maps* false)

(defn pps [& xs]
  (for [x xs]
    (with-out-str
      (clojure.pprint/pprint x))))

(defn pp [& xs]
  (for [x xs]
    (clojure.pprint/pprint x)))

(defn extend-with-common-resource-options [resource-model]
  (-> resource-model
      (update-in [:produces] #(conj (or % []) {:media-type
                                               #{"text/plain;q=0.9"
                                                 "application/edn;q=0.7"
                                                 "application/json;q=0.8"}}))
      (assoc-in [:properties :last-modified]
                (new java.util.Date))))

(def Int schema/Int)
(def IntPos (schema/both Int (schema/pred pos? 'pos?)))

(def resources
  {:blog.api/books
   {[:methods :get :parameters :query (schema/optional-key :author)] #(or % IntPos)}})

(defn extend-with-resource-options [resource-model]
  (if-let [resource-config (get resources (:id resource-model))]
    (loop [paths (keys resource-config)
           res   resource-model]
      (if (empty? paths)
        res
        (recur
         (rest paths)
         (update-in res (first paths) (get resource-config (first paths))))))
    resource-model))

(defmethod ig/init-key ::item-blob
  [[_ id] _]
  (log/info ::item-blob " initialized")
  (yada/resource
   (->
    {:id         id
     :parameters {:path {:id IntPos}}
     :methods
     {:get
      {:response
       (fn [ctx]
         (log/info ::item-blob.get.response " handler started")
         (let [data   (db/select {:entity (keyword (name id))
                                  :id     (-> ctx :parameters :path :id)})
               result (if (nil? data)
                        (-> (:response ctx)
                            (assoc :status 404)
                            (assoc :body {:message "Not found"}))
                        data)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}}
    extend-with-common-resource-options
    extend-with-resource-options)))

(defmethod ig/init-key ::items-list
  [[_ id] _]
  (yada/resource
   (->
    {:id id
     :methods
     {:get
      {:parameters {:query {(schema/optional-key :order) schema/Str
                            (schema/optional-key :limit) IntPos}}
       :response
       (fn [ctx]
         (let [query  (-> ctx :parameters :query)
               result (db/select {:entity (keyword (name id))
                                  :query  (or query {})})]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}}
    extend-with-common-resource-options
    extend-with-resource-options)))

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

(defmethod ig/init-key ::root
  [_ x]
  (yada/as-resource x))
