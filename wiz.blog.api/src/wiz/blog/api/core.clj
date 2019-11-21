(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [yada.yada :as yada]
   [yada.swagger :as swagger]
   [wiz.blog.api.db.books :as books]
   [wiz.blog.api.db.authors :as authors]
   [integrant.core :as ig]))

(defn pps [x] (with-out-str (clojure.pprint/pprint x)))
(defn pp [x] (clojure.pprint/pprint x))

(defn string-resource
  [x]
  (yada/as-resource x))

(defmethod ig/init-key ::root
  [_ x]
  (string-resource x))

(defn books [opts db]
  (yada/resource
   (merge
    opts
    {:methods
     {:get
      {:response
       (fn [ctx]
         (let [result (books/select-all db)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defn book [opts db]
  (yada/resource
   (merge
    opts
    {:methods
     {:get
      {:response
       (fn [ctx]
         (let [response (:response ctx)
               book-id  (-> ctx :parameters :path :id)
               data     (books/select-by-id db {:id (read-string book-id)})
               result   (if (nil? data)
                          (-> response
                              (assoc :status 404)
                              (assoc :body {:message "Not found"}))
                          data)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defn authors [opts db]
  (yada/resource
   (merge
    opts
    {:methods
     {:get
      {:response
       (fn [ctx]
         (let [result (authors/select-all db)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defn author [opts db]
  (yada/resource
   (merge
    opts
    {:methods
     {:get
      {:response
       (fn [ctx]
         (let [response (:response ctx)
               book-id  (-> ctx :parameters :path :id)
               data     (authors/select-by-id db {:id (read-string book-id)})
               result   (if (nil? data)
                          (-> response
                              (assoc :status 404)
                              (assoc :body {:message "Not found"}))
                          data)]
           (case (yada/content-type ctx)
             "text/plain" (with-out-str (clojure.pprint/pprint result))
             result)))}}})))

(defn defaults [opts]
  (merge
   {:produces   [{:media-type
                  #{"text/plain;q=0.9"
                    "application/edn;q=0.7"
                    "application/json;q=0.8"}}]
    :properties {:last-modified (new java.util.Date)}}
   opts))

(defmethod ig/init-key ::books
  [_ db]
  (-> {:id ::books :description "List of Books"}
      (defaults)
      (books db)))

(defmethod ig/init-key ::book
  [_ db]
  (-> {:id ::book :description "Full book info"}
      (defaults)
      (book db)))

(defmethod ig/init-key ::authors
  [_ db]
  (-> {:id ::books :description "List of Books"}
      (defaults)
      (authors db)))

(defmethod ig/init-key ::author
  [_ db]
  (-> {:id ::book :description "Full book info"}
      (defaults)
      (author db)))

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
