(ns blog.layout
  (:require
   [hiccup.page :as hiccup]))

(defn apply-header-footer [page]
  (hiccup/html5 {:lang "en"}
    [:head
     [:title "Gabriel Marin"]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:link {:type "text/css"
             :href "/public/css/style.css"
             :rel "stylesheet"}] ;; css
     [:body
      [:div {:class "header"}
       [:div {:class "name"}
        [:a {:href "/"} "(Gabriel Marin)"]
        [:div {:class "header-right"}
         [:a {:href "/public/about.html"} "About"]]]]
      page]
     [:footer
      [:p "Copyright © 2023-2024 Gabriel Marin"]]]))

