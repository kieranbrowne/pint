(ns pint.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r :refer [atom cursor]]
   ))



(defonce debug?
  ^boolean js/goog.DEBUG)

(defonce state
  (atom
   {:page :home}))


(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  ;; (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state assoc :page :home))

  (defroute "/component/:type/:key" [type key]
    (do
      (swap! state assoc :page :component)
      ; (swap! state assoc :component {:type type :key key})
      ))

  (hook-browser-navigation!))



;; Pages

(defn home [ratom]
  [:div [:h1 "Home Page"]
   [:p "FIXME"]
   ; [:p (hiccup/html [:script])]
   [:a {:href "/component/type/key"} "about page"]])

(defn component-page [ratom]
  [:div [:h1 "Component Page"]
   [:a {:href "/"} (str (:component @state))]])


;; Initialize App

(defmulti page identity)
(defmethod page :home [] home)
(defmethod page :component [] component-page)
(defmethod page :default [] (fn [_] [:div]))

(defn current-page [ratom]
  (let [page-key (:page @ratom)]
    [(page page-key) ratom]))

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (r/render [current-page state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (app-routes)
  (reload))
