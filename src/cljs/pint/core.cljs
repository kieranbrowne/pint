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
   [pint.style :as style]
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

  (defroute "/docs" []
    (swap! state assoc :page :docs))

  (defroute "/library" []
    (do
      (swap! state assoc :page :library)
      (reset! (cursor state [:component]) {:type nil :key nil})
      ))

  (defroute "/library/:type/:key" [type key]
    (do
      (swap! state assoc :page :component)
      (reset! (cursor state [:component]) {:type type :key key})
      ))

  (defroute "*" []
    (swap! state assoc :page :home))

  (hook-browser-navigation!))


;; (.log js/console
;;   )
(def grid
  (let [ls [1 2 3 4 6 12]
        ns (map vector ls (reverse ls))]
    [:div.grid.df.fww.jcsb.ptg
     {:style {:background-color "rgba(55,132,212,.2)"}}
     (for [i ns]
       (conj
        (for [x (range (first i))]
          [:div.dib.lh2rem.mbg.tac.cw
           {:class (str "w" (second i) "c")
            :style {:background-color "rgba(55,132,212,.5)"}}
           (str ".w" (second i) "c")
           ]
          )
        [:div.w12c])
       )
     ]))

(def grid-squares
  [:div.grid.df.fww
   {:style {:background-color "rgba(55,132,212,.2)"}}
     [:div.dib.lh1rem.mrg.tac.cw.bsbb.ptg.w2c.h1c
      {:style {:background-color "rgba(55,132,212,.5)"}}
      ".w2c.h1c"]
   [:div.dib.lh1rem.mrg.tac.cw.bsbb.ptg.w1c.h2c
    {:style {:background-color "rgba(55,132,212,.5)"}}
    ".w1c\n.h2c"]
   [:div.dib.lh1rem.mrg.tac.cw.bsbb.ptg.w2c.h2c
    {:style {:background-color "rgba(55,132,212,.5)"}}
    ".w2c.h2c"]
   [:div.dib.lh1rem.mrg.tac.cw.bsbb.ptg.w6c.h2c
    {:style {:background-color "rgba(55,132,212,.5)"}}
    ".w2c.h2c"]
   ])



(defn tabs [curs options]
  (fn []
    [:div.df.w100%
     (for [option options]
       [:div.lh2rem.plg.prg.cp
              {:on-click #(reset! curs option)
               :style (if (= @curs option) {:background-color "#aaa"} {:background-color "#eee"})
               }
              (name option)])]))

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
     [(tabs mode [:html :edn])]
     [:a#test {:on-click #(.select (.getElementById js/document "test"))} "Copy to clipboard"]
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

(defn header []
  [:div {:style {:border-bottom "1px solid #ccc"}}
   [:a {:href "#"}
    [:div.w3c.dib.mlg.cb.fw400 "Pint " [:span.fs12px.cg.fw100 "v0.1.0"]]]
   [:div.w9c.dib.mlg.tar.lh2rem
    [:a.fs6.mlg.tdn {:href "#/docs" :style style/link} "Docs"]
    [:a.fs6.mlg.tdn {:href "#/library" :style style/link} "Library"]
    [:a.fs6.mlg.tdn {:href "https://github.com/kierantbrowne/pint" :style style/link} "Github"]
    ]
   ]
  )

(defn home-page [ratom]
  [:div
   [header]
   [:div.w100vw.h100vh {:style {:background "white"}}
    [:h2.cw.tac.fw100.m0.ls1px.fs32px "microclasses"]]

   [:div.pt5rem.w12c.mlg
    [:h2.fs3rem.m0.cg "Grids"]
    [:p.1rem.mw30em "Grids are one of the most important parts of any css project."]
    grid
    [:h2 "The value c is not exclusive to width"]
    grid-squares
    ]

   [:div.pt5rem.w12c.mlg
    [:h2.fs3rem.m0.cg "Font Sizing"]
    [:p.1rem.mw30em "Font sizes which make sense"]
    (for [i (range 1 7)]
      [:p.m0 {:class (str "fs" i)} "lorem ipsum"])]
   ])

(defn docs-page [ratom]
  [:div
   [header]
   [:div
    (for [i (range 1 7)]
      [:p {:class (str "fs" i)} "Lorem ipsum"])
    ]
   [:div.mlg.w3c
    [:h4.fw400 "Layouts"]
    [:ul.lsn.pl0 [:li [:a.fs4 {:href "#/docs"} "Layouts"]]]
    ]
   ])

(defn component-page [ratom]
  (let [key (-> @state :component :key)
        type (-> @state :component :type)
        component (cursor state [:library type key])]
    [:div
     [:h1 (:title @component)]
     [:p (str @state)]
     [:div {:style {:background-color "#ccc" :padding "2rem" :text-align "center"}}
      [:div.dib {:style {:box-shadow "0 0 2px 1px #aaa"
                         :background-color "white" :text-align "left"}}
       (:structure @component)
       ]
      ]
     [code-editor (cursor component [:structure])]
     [:a {:href "#"} "Home"]
     ]))

(defn library-page [ratom]
  (let [key (-> @state :component :key)
        type (-> @state :component :type)
        component (cursor state [:library type key])]
    [:div
     [header]
     [:div.w2c.mlg.dib.h100vh.pt6px.vat
      {:style {:border-right "1px solid #ccc"}}
      (for [[type v] library]
        [:div
         [:h3.fs5.mb0.lh1rem (clojure.string/capitalize type)]
         (for [[key comp] v]
           [:a.fs6.lh1rem.db.tdn
            {:href (str "#/library/" type "/" key)
             :style style/link
             }
            (:title comp)]
           )]
        )
      ]
     [:div.w10c.mlg.dib.bsbb.pl2rem.pr2rem
      [:h1 (:title @component)]
      [:div {:style {:background-color "#ccc" :padding "2rem" :text-align "center"}}
       [:div.dib {:style {:box-shadow "0 0 2px 1px #aaa"
                          :background-color "white" :text-align "left"}}
        (:structure @component)
        ]
       ]
      [code-editor (cursor component [:structure])]]
     ]))


;; Initialize App

(defmulti page identity)
(defmethod page :home [] home-page)
(defmethod page :docs [] docs-page)
(defmethod page :library [] library-page)
(defmethod page :component [] library-page)
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
