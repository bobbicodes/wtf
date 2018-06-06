(ns wtf.core
  (:require [ring.middleware.params :as params]
            [ring.util.response :as ring]
	    [clojure.data.json :as json]
	    [ring.adapter.jetty :as jetty]
            [hiccup.page :as page]))

(defn wtf [x]
  (:extract (json/read-str (slurp
    (str "https://en.wikipedia.org/api/rest_v1/page/summary/" x)) :key-fn keyword)))

(defn pic [x]
  (second (first (:originalimage (json/read-str (slurp
    (str "https://en.wikipedia.org/api/rest_v1/page/summary/" x)) :key-fn keyword)))))

(defn page [name]
  (page/html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
    [:title "WTF?"]
    (page/include-css "base.css"
                 "skeleton.css"
                 "screen.css")
    (page/include-css "http://fonts.googleapis.com/css?family=Sigmar+One&v1")]
   [:body
    [:div {:id "header"}
     [:h1 {:class "container"} "WTF?"]]
    [:div {:id "content" :class "container"} (if name
         (str "<form>"
              "WTF is...  <input name='name' type='text'> ? "
              "<input type='submit'>"
              "</form><br>" (wtf name) "<img src=" (pic name) ">")
         (str "<form>"
              "Name: <input name='name' type='text'>"
              "<input type='submit'>"
              "</form>"))]]))
       

(defn handler [{{name "name"} :params}]
  (-> (ring/response (page name))
      (ring/content-type "text/html")))

(def app
  (-> handler params/wrap-params))

(jetty/run-jetty app {:port 8080})
