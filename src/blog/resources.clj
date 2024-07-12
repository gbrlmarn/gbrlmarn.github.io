(ns blog.resources
  (:require [blog.layout :as layout]
            [blog.posts :as posts]
            [blog.database :as database]
            [clojure.string :as str]
            [markdown.core :as md]
            [mapdown.core :as mp]
            [stasis.core :as stasis]
            [optimus.assets :as assets]
            [datomic.api :as d]))

(defn get-assets []
  (assets/load-assets "public" ["/cv/gmarin.pdf"]))

(defn read-and-convert-posts! [src db]
  (let [data  (stasis/slurp-directory src #"/posts/.*\.md$")
        posts-paths (map posts/key-to-html (keys data))
        posts-content (posts/format-posts db)]
    (zipmap posts-paths posts-content)))

(defn get-public-map []
  (let [data (stasis/slurp-directory "resources"  #"/public/.*\.md$")
        public-paths (map #(str/replace % #".md" ".html") (keys data))
        public-contents (map md/md-to-html-string (vals data))]
    (zipmap public-paths public-contents)))

(defn get-css [src]
  (stasis/slurp-directory src #".*\.css$"))

(defn format-pages [m db]
  (let [up-m (assoc m "/index.html" (posts/posts-titles-page db))
        up-m (into up-m (get-public-map))
        html-keys (keys up-m)
        html-vals (map layout/apply-header-footer (vals up-m))]
    (zipmap html-keys html-vals)))

(defn merge-website-assets! [db]
  (let [page-map (format-pages (read-and-convert-posts! "resources" db)
                               db)
        css-map (get-css "resources")]
    (stasis/merge-page-sources {:css css-map
                                :pages page-map})))

(defn get-pages [db]
  (merge-website-assets! db))

(comment
  )
