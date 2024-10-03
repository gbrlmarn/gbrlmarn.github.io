(ns blog.cv
  (:require [clj-pdf.core :as pdf]))

(def employees
  [{:occupation "Software Engineer",
    :name "Luxoft"
    :period "2022 - Present"
    :tasks
    ["Maintained project building environment using CMake."
     "Done socket programming using ZeroMQ library in C/C++."
     "Automated environment configuration using Docker and Bash."
     "Created integration tests using Golang and Clojure."
     "Developed command line interface for sending/receiving messages."
     "Developed certificate viewer in C++ using OpenSSL API."  
     "Refactored multiple repositories according to coding guidelines."]}
   {:occupation "Software Engineer",
    :name "Continental"
    :period "2021 - 2022"
    :tasks
    ["Worked on XML generator using Java and AUTOSAR library." 
     "Converted JSON to XML data using Python."]}
   {:occupation "System Engineer",
    :name "Ministry of Internal Affairs"
    :period "2019 - 2021"
    :tasks
    ["Developed scripts for task automation with PowerShell." 
     "Deployed and configured Radius server and Squid proxy."]}
   {:occupation "System Engineer",
    :name "Ministry of Internal Affairs"
    :period "2018 - 2019"
    :tasks
    ["Provided layer 1 support."                              
     "Managed users access in Oracle SQL database."]}])

(def employee-template
  (pdf/template
   [:paragraph {:align :left}
    [:subscript {:size 12} ;; subscript for better alignment
     (str $occupation " " $period "\n")]
    [:phrase {:size 8 :color [155 155 155]
              :style :italic}
     $name]
    [:list {:size 10}
     (flatten $tasks)]]))

(def links
  [{:target "https://github.com/gbrlmarn"
    :name "github"}
   {:target "https://gbrlmarn.github.io"
    :name "website"}])

(def link-template
  (pdf/template
   [:anchor
    {:style {:underlines true
             :color [0 0 200]}
     :target $target}
    $name]))

(defn export []
  (pdf/pdf
   [{:title "Gabriel Marin"
     :size :a4
     :font {:size 12 :family :helvetica}
     :footer {:page-numbers false}}
    [:heading {:style {:size 16}} "Gabriel Marin"]
    [:line]
    [:list {:size 10}
     (link-template links)]
    [:heading {:style {:size 14}} "Work"]
    [:line]
    (employee-template employees)
    [:heading {:style {:size 14}} "Skills"]
    [:line]]
   "./resources/public/cv/marin.pdf"))
