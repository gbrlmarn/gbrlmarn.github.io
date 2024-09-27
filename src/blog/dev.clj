(ns blog.dev
  (:require [blog.core :as core]
            [clojure.tools.namespace.repl :as repl]
            [mount.core :as mount :refer [defstate]]
            [prone.middleware :as prone]
            [ring.adapter.jetty :as jetty]))

(repl/set-refresh-dirs "src")

(defstate server
  :start (jetty/run-jetty
          (-> (core/app-handler)
              prone/wrap-exceptions)
          {:port 3030
           :join? false})
  :stop (.stop server))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn restart []
  (stop)
  (start))

(defn reload []
  (stop)
  (repl/refresh :after 'blog.dev/start))

(defn -main [& args]
  (start))

(comment
  (stop)
  (start)
  (restart)
  )
