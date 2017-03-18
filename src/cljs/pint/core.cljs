(ns pint.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r :refer [atom cursor]]
   [pint.library :refer [library]]
   ))



(defonce debug?
  ^boolean js/goog.DEBUG)

(defonce state
  (atom
   {:page :home
    :component {:type "unset" :key "unset"}
    }))

(def component (cursor state [:component]))


(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))


(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (swap! state assoc :page :home))

  (defroute "/component/:type" [type]
    (do
      (swap! state assoc :page :component)
      (reset! component {:type type})
      ))

  (defroute "/component/:type/:key" [type key]
    (do
      (swap! state assoc :page :component)
      (reset! component {:type type :key key})
      ))

  (defroute "*" []
    (swap! state assoc :page :home))

  (hook-browser-navigation!))



;; Pages

(defn home [ratom]
  [:div
   [:div.w100vw.h100vh {:style {:background "blue"}}
    [:h1.m0.pt40vh.cw.tac.fwb.ls1px.fs72px "Pint.css"]
    [:h2.cw.tac.fw100.m0.ls1px.fs32px "microclasses"]]
   [:div.pt5rem
    [:h2.fs3rem.m0.cg "Grids"]
    [:p.1rem.mw30em "Grids are one of the most important parts of any css project."]
    ]
   [:h1.fw100 "Home Page"]
   [:p.fwb "FIXME"]
   [:p (str library)]
   ; [:p (hiccup/html [:script])]
   [:a {:href "/#/component/tiles/basic-post"} "test page"]])

(defn component-page [ratom]
  (let [key (:key @component)
        type (:type @component)
        component (get (get library type) key)]
  [:div [:h1 (:title component)]
   (str component)
   [:p (str @state)]
   [:a {:href "/#/"} "Home"]
   ])
  )


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
