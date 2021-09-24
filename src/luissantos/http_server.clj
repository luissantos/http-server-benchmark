(ns luissantos.http-server
  (:use org.httpkit.server)
  (:require
   [reitit.ring :as ring]
   [jsonista.core :as json]
   [ring.util.response :as response]
   [luissantos.game :as game]
   )
  (:gen-class))

(set! *warn-on-reflection* true)
(def n-cpu (.availableProcessors (Runtime/getRuntime)))


(defn handle-game
  ""
  [game-fn]
  (fn [request]
               (->
                (json/read-value (:body request) json/keyword-keys-object-mapper)
                ((resolve game-fn))
                (json/write-value-as-string)
                (response/response)
                (response/header "content-type" "application/json"))))

(def handler
  (ring/ring-handler
   (ring/router
    [
     ["/end" {:post (handle-game 'luissantos.game/end)}]
     ["/move" {:post (handle-game 'luissantos.game/move) }]
     ["/start" {:post (handle-game 'luissantos.game/start)}]
     ["/"
      {:get (fn [request]
              (-> (game/root request)
                  (json/write-value-as-string)
                  (response/response)
                  (response/header "content-type" "application/json")))}]
     ])))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& args]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and https://http-kit.github.io/migration.html#reload
  (reset! server (run-server #'handler {:port 8080  :thread (* n-cpu 1) })))

(comment


  (stop-server)
  )
