(ns pint.utils
  (:require [garden.core :as garden :refer [css]]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

(defn unit [unit value]
  (str value unit))

(defn comb [props values]
  (map (fn [x] {:prop (first x) :value(last x)}) 
    (combo/cartesian-product props values)))

(defn cssify 
  [combos]
  (map (fn [combo]
         (css
           [(str "."
                 (-> combo :prop first)
                 (-> combo :value first))

            { (-> combo :prop last)
              (-> combo :value last) }

            ])
         ; (css [(str (:short (:prop combo)))])
         )
    combos))

(def zip (partial map vector))

; auto shortener
(defn shorten [property]
  (str/join (map first
    (filter 
      (complement empty?) 
      (str/split (str property) #":|-"))))
  )

(def splitter (partial re-seq #"\w+|\d(?!\S)|%"))

(splitter "display-inline:block1,10:")

(defn shorten-value [value]
  (str/join
   (map first
        (filter 
         (complement empty?) 
         (str/split (str property) #":|-"))))
  )

(map splitter 
     ["100px" "10%" "block" "inline-block" "left" "100vh" "none"]
     )

(defn auto-val-shortener [property]
  (str/join (map first
    (filter 
      (complement empty?) 
      (str/split (str "20rem") #":|-"))))
  )


(def pints
  {:.db {:display "block"}
   :.dib {:display "inline-block"}
   :.fw100 {:font-weight "100"}
   })

(spit "resources/public/css/pint-12-30px.css"
      (apply str (map css (vec pints))))

;; example structure
(def post-tile
  [:div.db.fw100 "My Post"
   [:div.dib "pants"]
   ]
  )

(def classes
  "get classes from hiccup keyword"
  (comp
   (partial map keyword)
   (partial re-seq #"\.[^\.]+")
   str))

(map classes post-tile)

(defn collate-styles [hiccup-key]
  (apply merge
         (map (partial get pints)
              (classes hiccup-key))))

(gensym "pint")

(defn traditional-css
  "Generate traditional nested css structure from markup."
  [markup]
  (into
   [(first markup)
    (collate-styles (first markup))]
   (map traditional-css (filter vector? (rest markup)))
   ))

(css (traditional-css post-tile))
(traditional-css post-tile)

