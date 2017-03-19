(ns pint.core
  (:require-macros [secretary.core :refer [defroute]]
                   [clojure.string :as str])
  (:import goog.History)
  (:require
   [secretary.core :as secretary]
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [reagent.core :as r :refer [atom cursor]]
   [pint.library :refer [library]]
   [hickory.core :as hickory]
   ))



(defonce debug?
  ^boolean js/goog.DEBUG)

(defonce state
  (atom
   {:page :home
    :component {:type "unset" :key "unset"}
    :library library
    :view {:code-editor {:mode :html}}
    }))

;; (def component (cursor state [:component]))


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


  (defroute "/component/:type/:key" [type key]
    (do
      (swap! state assoc :page :component)
      (reset! (cursor state [:component]) {:type type :key key})
      ))

  (defroute "*" []
    (swap! state assoc :page :home))

  (hook-browser-navigation!))


;; (.log js/console
;;   )

(defn html-to-reagent [html]
  (first
   (map (comp
         (partial clojure.walk/postwalk
                  #(if (map? %)
                     (dissoc %
                             :data-reactroot
                             :data-reactid
                             :data-react-checksum) (identity %)))
         hickory/as-hiccup)
        (hickory/parse-fragment html)))
  )

(def reagent-to-html
  (comp
   #(clojure.string/replace % #"(\w?\"?)\s*" "$1")
   #(clojure.string/replace % #"<!-- /?react-text:? \d? ?-->" "")
   #(clojure.string/replace % #"data-react[\w|-]+=\"-?\d*\"" "")
   r/render-to-string))

(defn code-editor [value]
  (let [mode (cursor state [:view :code-editor :mode])
        read-fn (case @mode
                  :edn str
                  :html reagent-to-html)
        write-fn (case @mode
                   :edn cljs.reader/read-string
                   :html html-to-reagent)]
    [:div
     [:a {:on-click
          #(reset! mode :edn)}
      "EDN" (if (= @mode :edn) "tick")]
     [:a {:on-click
          #(reset! mode :html)}
      "HTML" (if (= @mode :html) "tick")]
    [:textarea.fs18px.pt1rem.an.bn.m1rem
     {
      :type "text"
      :style {:background-color "#eee"}
      :rows 4
      :cols 50
      :value (try (read-fn @value)
                  (catch :default e (str @value))
                  )
      :on-change
      #(reset! value
               (try
                 (if (= (write-fn (-> % .-target .-value))
                        (write-fn (read-fn (write-fn (-> % .-target .-value))))
                        )
                   (write-fn (-> % .-target .-value))
                   (str (-> % .-target .-value))
                   )
                 (catch :default e (str (-> % .-target .-value)))))}
     ;; (read-fn @value)
      ]]))

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
   [:p (str (-> @state :library))]
   ; [:p (hiccup/html [:script])]
   [:a {:href "/#/component/tiles/basic-post"} "test page"]])

(defn component-page [ratom]
  (let [key (-> @state :component :key)
        type (-> @state :component :type)
        component (cursor state [:library type key])]
  [:div [:h1 (:title @component)]
   [:p (str @state)]
   [:div {:style {:background-color "#ccc" :padding "12px"}}
    [:div {:style {:box-shadow "0 0 2px 1px #aaa"
                   :background-color "white"}}
     (:structure @component)
     ]
    ]
   [code-editor (cursor component [:structure])]
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
