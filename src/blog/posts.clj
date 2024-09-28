(ns blog.posts
  (:require [blog.database :as database]
            [blog.time :as time]
            [datomic.api :as d]
            [clojure.string :as str]
            [hiccup.core :as hiccup]
            [markdown.core :as md]
            [mapdown.core :as mp]
            [emoji.core :as emoji]
            ))

(defn get-posts-map [db]
  (map #(into {} %)
       (d/q database/posts-map-q db)))

(defn key-to-html [s]
  (str/replace s #".md" ".html"))

(defn format-posts-titles [paths titles]
  (hiccup/html
   [:h4
    [:a {:href
         (key-to-html paths)}
    titles]]))

(defn posts-titles-page [db]
  (let [posts-map (get-posts-map db)]
    (hiccup/html
        [:ul
         (map format-posts-titles
              (map #(:post/path %) posts-map)
              (map :post/title posts-map))])))

(defn format-posts-time [posts-map]
  (map #(time/ymd
         (time/->ldt
          (clojure.instant/read-instant-date (:post/time %))))
       posts-map))

(defn format-posts-body [posts-map]
  (map md/md-to-html-string
       (map #(emoji/emojify (:post/body %))
            posts-map)))

(defn format-posts [db]
  (let [posts-map (get-posts-map db)
        posts-time (format-posts-time posts-map)
        posts-body (format-posts-body posts-map)]
    (map #(hiccup/html %1 [:div "Published " %2])
         posts-body posts-time)))

(comment
  (let [conn (database/db-conn)
        db (d/db conn)
        posts-map (map #(into {} %) (d/q database/posts-map-q db))
        posts-body (map :post/body posts-map)
        posts-time (map :post/time posts-map)]
    #_(map #(hiccup/html [%1 [:div "Published " %2]])
         (map md/md-to-html-string posts-body)
         (map #(blog.time/ymd (blog.time/->ldt (clojure.instant/read-instant-date %))) posts-time))
    (map #(blog.time/->ldt (clojure.instant/read-instant-date %)) posts-time)
    )

  (let [conn (database/db-conn)
        db (d/db conn)
        posts-map (map #(into {} %) (d/q database/posts-map-q db))
        posts-body (map :post/body posts-map)
        posts-time (map :post/time posts-map)]
    (map #(hiccup/html [%1 [:div "Published " %2]])
         (map md/md-to-html-string posts-body)
         posts-time)
    ;;(map #(blog.time/->ldt #inst %) posts-time)
    posts-time
    )
  
  (let [conn (database/db-conn)
        db (d/db conn)
        posts-map (map #(into {} %) (d/q database/posts-map-q db))]
    posts-map)

  (let [conn (database/db-conn)
        db (d/db conn)
        posts-map (get-posts-map db)
        posts-time (map #(time/ymd
                          (time/->ldt
                           (clojure.instant/read-instant-date (:post/time %))))
                        posts-map)
        posts-body (map md/md-to-html-string
                        (map #(:post/body %)
                             posts-map))]
    #_(map #(hiccup/html [%1 [:div "Published " %2]])
           posts-body posts-time)
    (map #(hiccup/html %1 [:div "Published " %2]) posts-body posts-time)
    )

  (-> (java.io.File. "resources/posts/hand-position-when-writing.md")
      .lastModified)

  (-> (java.io.File. "resources/posts/hand-position-when-writing.md")
      .toPath)

  (java.nio.file.attribute.BasicFileAttributes )

  (-> (java.nio.file.Path "resources/posts/hand-position-when-writing.md"))

  (let [conn (database/db-conn)
        db (d/db conn)
        posts-map (map #(into {} %) (d/q database/posts-map-q db))]
    ;;(map #(blog.time/->ldt #inst (:post/time %)) posts-map)
    (map #(:post/time %) posts-map)
    )
  (blog.time/ymd 
   (blog.time/->ldt (clojure.instant/read-instant-date "2023-05-06")))
  
  
  (defn format-posts [posts]
    (map md/md-to-html-string
         (map #(:post/body (mp/parse %))
              posts)))

  (defn add-date [posts]
    (map #(hiccup/html5 [%1 [:div "Published" %2]])
         posts (:posts/time posts)))
  )
