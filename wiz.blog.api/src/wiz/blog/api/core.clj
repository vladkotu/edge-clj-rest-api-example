(ns wiz.blog.api.core
  (:require
   [clojure.tools.logging :as log]
   [yada.yada :as yada]
   [yada.swagger :as swagger]
   [integrant.core :as ig]))

(defn pps [x] (with-out-str (clojure.pprint/pprint x)))

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
    {:id          ::books
     :produces    [{:media-type
                    #{"text/plain"
                      "application/edn;q=0.9"
                      "application/json;q=0.9"}}]
     :properties  {:last-modified (new java.util.Date)}
     :methods
     {:get
      {:response
       (fn [ctx] (let [rq     (->> [:request] (get-in ctx))
                       rs     (->> [:response] (get-in ctx))
                       ps     (->> [:parameters] (get-in ctx))
                       result {:db db :parameters ps :yada-con (yada/content-type ctx) :req rq :res rs}]
                         ;; (with-out-str (clojure.pprint/pprint {:yada-con (yada/content-type ctx) :req rq :res rs}))
                   (case (yada/content-type ctx)
                     "text/plain" (with-out-str (clojure.pprint/pprint result))
                     result)))}}})))

(defmethod ig/init-key ::books
  [_ db]
  (books {:description "List of Books"} db))

(defmethod ig/init-key ::book
  [_ db]
  (books {:description "Full book info"} db))

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
