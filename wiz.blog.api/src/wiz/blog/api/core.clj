(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [schema.core :as schema]
   [yada.yada :as yada]
   [yada.swagger :as swagger]
   [wiz.blog.api.db :as db]
   [integrant.core :as ig]))

;;;;;;;;;;;
;; Tools ;;
;;;;;;;;;;;
;; (set! *print-namespace-maps* false)
(defn pps [& xs]
  (for [x xs]
    (with-out-str
      (clojure.pprint/pprint x))))

(defn pp [& xs]
  (for [x xs]
    (clojure.pprint/pprint x)))

;;;;;;;;;;;;;
;; Schemes ;;
;;;;;;;;;;;;;
(def Int schema/Int)
(def IntPos (schema/both Int (schema/pred pos? 'pos?)))
(schema/defschema Book
  {:title     String
   :author-id IntPos})

(schema/defschema Author
  {:name      String
   :email     String
   :nickname  (schema/maybe String)
   :biography (schema/maybe String)})

(schema/defschema Comment
  {:message   String
   :author-id IntPos
   :book-id   IntPos})

;;;;;;;;;;;;;;;
;; Resources ;;
;;;;;;;;;;;;;;;
(def common-resources-config
  {[:consumes]
   #(conj (or % [])
          {:media-type
           #{"application/x-www-form-urlencoded"
             "application/edn;q=0.7"
             "application/json;q=0.8"}})

   [:produces]
   #(conj (or % [])
          {:media-type
           #{"text/plain;q=0.9"
             "application/edn;q=0.7"
             "application/json;q=0.8"}})

   [:properties :last-modified]
   #(or % (new java.util.Date))})

(def resource-errors
  {:blog.api/books
   {:already-exists "Cannot create book as it's already exists."}
   :blog.api/authors
   {:already-exists "Cannot create author as it's already exists."}
   :blog.api/comments
   {:already-exists "Cannot create comment as it's already exists."}})

(def resources-config
  {:blog.api/books
   {[:methods :get :parameters :query (schema/optional-key :author)] #(or % IntPos)
    [:methods :post :parameters :body] #(or % Book)}
   :blog.api/authors
   {[:methods :post :parameters :body] #(or % Author)}
   :blog.api/comments
   {[:methods :post :parameters :body] #(or % Comment)}})

(defn update-resources [resource-model config]
  (loop [paths (keys config)
         res   resource-model]
    (if (empty? paths)
      res
      (recur
       (rest paths)
       (update-in res (first paths) (get config (first paths)))))))

(defn extend-with-common-resource-options [resource-model]
  (update-resources resource-model common-resources-config))

(defn extend-with-resource-options [resource-model]
  (if-let [config (get resources-config (:id resource-model))]
    (update-resources resource-model config)
    resource-model))

(defn entity-id [ctx]
  (keyword (name (:id ctx))))

(defn get-list-of-entities [ctx]
  (let [query  (-> ctx :parameters :query)
        result (db/select {:entity (entity-id ctx)
                           :query  (or query {})})]
    (case (yada/content-type ctx)
      "text/plain" (with-out-str (clojure.pprint/pprint result))
      result)))

(defn get-entity [ctx]
  (log/info ::item-blob.get.response " handler started")
  (let [data   (db/select {:entity (entity-id ctx)
                           :id     (-> ctx :parameters :path :id)})
        result (if (nil? data)
                 (-> (:response ctx)
                     (assoc :status 404)
                     (assoc :body {:message "Not found"}))
                 data)]
    (case (yada/content-type ctx)
      "text/plain" (with-out-str (clojure.pprint/pprint result))
      result)))

(defn create-new-entity [ctx]
  (let [body   (-> ctx :parameters :body)
        result (db/insert {:entity (entity-id ctx)
                           :body   body})]
    (if (nil? result)
      (-> (:response ctx)
          (assoc :status 400)
          (assoc :body {:message (get-in resource-errors [(:id ctx) :already-exists])}))
      result)))

(defmethod ig/init-key ::item-blob
  [[_ id] _]
  (log/info ::item-blob " initialized")
  (yada/resource
   (->
    {:id         id
     :parameters {:path {:id IntPos}}
     :methods
     {:get
      {:response get-entity}}}
    extend-with-common-resource-options
    extend-with-resource-options)))

(defmethod ig/init-key ::items-list
  [[_ id] _]
  (yada/resource
   (->
    {:id id
     :methods
     {:post {:response create-new-entity}
      :get  {:parameters {:query {(schema/optional-key :order) schema/Str
                                  (schema/optional-key :limit) IntPos}}
             :response   get-list-of-entities}}}
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
