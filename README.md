# wtf

The "minimum viable product", or, "what's the stupidest thing I can build that's still slightly useful?"

It's a stupid answer engine. You type something in and it gives you a brief definition and a picture (for most things). That's it. Go ahead, [try it.](https://dry-retreat-70804.herokuapp.com/) It's SO much stupider than Google.

SPOILER: It simply calls the Wikipedia API and returns the description and full-res image for the query.

## The entire code:

```
(ns wtf.core
  (:require [ring.middleware.params :as params]
            [ring.util.response :as ring]
	    [clojure.data.json :as json]
	    [ring.adapter.jetty :as jetty]
            [hiccup.page :as page])
(:gen-class))

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
    [:title "WTF?"]]
   [:body {:bgcolor "#E6E6FA"}
[:center    
[:div {:id "header"}
     [:h1 "WTF?"]]
    [:h3 (if name
         (str "<form>"
              "WTF is...  <input name='name' type='text'> ? "
              "<input type='submit'>"
              "</form><br>" (wtf name) "<br><br><img src=" (pic name) " width="1200">")
         (str "<form>"
              "WTF is... <input name='name' type='text'>"
              "<input type='submit'>"
              "</form>"))]]]))
       
(defn handler [{{name "name"} :params}]
  (-> (ring/response (page name))
      (ring/content-type "text/html")))

(def app
  (-> handler params/wrap-params))

(defn start [port]
  (jetty/run-jetty app {:port port
                          :join? false}))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
    (start port)))
```
