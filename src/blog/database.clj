(ns blog.database
  (:require [clojure.java.io :as io]
            [datomic.api :as d]
            [mapdown.core :as md]
            [stasis.core :as stasis]))

(defn get-posts [src]
  (let [data (stasis/slurp-directory src #"/posts/.*\.md")
        post-path (keys data)
        post-content (vals data)]
    (zipmap post-path post-content)))

;; TODO: To much is done in this function...
;; Needs refactoring
(defn add-posts [conn]
  (let [posts-map (get-posts "resources")
        posts-path (keys posts-map)
        posts-content (vals posts-map)
        posts (map #(md/parse (str %1 %2))
                   (map #(str ":post/path " % "\n")
                        posts-path)
                   posts-content)]
    @(d/transact conn posts)))

(defn db-conn []
  (d/create-database "datomic:mem://blog")
  (let [conn (d/connect "datomic:mem://blog")]
    (d/transact conn (read-string (slurp (io/resource "schema.edn"))))
    conn))

(def all-titles-q '[:find ?post-title
                      :where
                    [_ :post/title ?post-title]])

(def all-times-q '[:find ?post-title
                      :where
                      [_ :post/time ?post-time]])

(def all-paths-q '[:find ?post-path
                     :where
                     [_ :post/path ?post-path]])

(def posts-map-q '[:find (pull ?e [:post/path :post/time
                                   :post/title :post/body])
                   :where [?e :post/time]])

(comment
  (d/delete-database "datomic:mem://blog")
  (d/create-database "datomic:mem://blog")
  (def conn (d/connect "datomic:mem://blog"))
  (d/transact conn (read-string (slurp (io/resource "schema.edn"))))
  (def db (d/db conn))
  (add-posts conn)
  (def db (d/db conn))

  (map #(md/parse (str %1 %2))
       (map #(str ":post/path " % "\n")
            (keys (get-posts "resources")))
       (vals (get-posts "resources")))
    
  (def path "posts/test.md")
  (def res (io/resource path))
  (def conn (d/connect "datomic:mem://blog"))
  (def db (d/db conn))
  
  @(d/transact conn [(md/parse (slurp res))])
  (def db (d/db conn))

  @(d/transact conn
               (map mapdown.core/parse
                    (vals (get-posts "resources"))))

  @(d/transact conn
               (map #(assoc {} :post/path %)
                    (keys (get-posts "resources"))))
  
  (def db (d/db conn))

  (def all-posts-q '[:find ?e
                     :where
                     [?e :post/title]])
  (def all-titles-q '[:find ?post-title
                      :where
                      [_ :post/title ?post-title]])
  (def all-paths-q '[:find ?post-path
                     :where
                     [_ :post/path ?post-path]])
  (def all-posts-body-q '[:find ?post-body
                          :where
                          [_ :post/body ?post-body]])

  (def posts-map-q '[:find ?post-path ?post-body
                     :where
                     [_ :post/path ?post-path]
                     [_ :post/body ?post-body]])

  (def posts-map-q '[:find (pull ?e [:post/path :post/body])
                     :where
                     [?e :post/path]])

  (d/q all-posts-q db)
  (d/q all-paths-q db)
  (d/q all-titles-q db)
  (d/q posts-map-q db)

  (->
   (d/q posts-map-q db)
   (first))

  (->
   (d/q posts-map-q db)
   )
 
  (map #(hiccup.core/html [:h1 (first %)])
       (d/q all-titles-q db))

  (map #(hiccup.core/html [:a {:href (first %1)} (first %2)])
       (d/q all-paths-q db)
       (d/q all-titles-q db))

  (d/q '[:find ?e
         :where
         [?e :post/title]]
       db)
  (d/q '[:find ?post-title
         :where
         [_ :post/title ?post-title]]
       db)

  (let [posts-map (get-posts "resources")])
  (.lastModified (java.io.File. (str "/resources" %)))

  )

