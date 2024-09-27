(ns blog.core
  (:require [blog.layout :as layout]
            [blog.resources :as resources]
            [blog.database :as database]
            [datomic.api :as d]
            [stasis.core :as stasis]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.export]
            [optimus.strategies :refer [serve-live-assets]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [prone.middleware :as prone]))

;; Rendering
(defn app-handler []
  (let [conn (database/db-conn)]
    (database/add-posts conn)
    (-> (stasis/serve-pages (resources/get-pages (d/db conn)))
        prone/wrap-exceptions
        (optimus/wrap resources/get-assets optimizations/all serve-live-assets)
        wrap-content-type)))

;; Exporting
(def out-dir "docs")

(defn export! [& args]
  (let [assets (optimizations/all (resources/get-assets) {})
        conn (database/db-conn)]
    (database/add-posts conn)
    (stasis/empty-directory! out-dir)
    (optimus.export/save-assets assets out-dir)
    (stasis/export-pages
     (resources/get-pages (d/db conn)) out-dir
     {:optimus-assets assets})
    (println "Website is ready!")))
