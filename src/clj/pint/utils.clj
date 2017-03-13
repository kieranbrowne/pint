(ns pint.utils
  (:require [garden.core :as garden :refer [css]]
            [clojure.math.combinatorics :refer [cartesian-product]]
            [clojure.string :as str]))

(defn unit [unit value]
  (str value unit))

(defn comb
  "Returns all combinations of properties and values"
  [props values]
  (map (partial apply hash-map)
    (cartesian-product props values)))

(def shorten
  "Auto shortener for properties and values"
  (comp
   (partial apply str)
   (partial map #(if (re-find #"\d" %) % ((comp str first) %)))
   (partial re-seq #"\w+|\d(?!\S)|%")
   str))

(def pint-class
  "Convert cssmap into pint microclass"
  (comp keyword (partial str ".") shorten))

(defn pintificate
  "Generate microclass for every combination of props and values."
  [props values]
  (apply merge
         (map
          (fn [x] {(pint-class x) x})
          (comb props values))))

(def pints
  "Collate all pints"
  (merge
   (pintificate [:display] [:inline-block :block :flex])
   (pintificate [:font-weight] (range 100 1000 100))
   (pintificate [:font-style] [:italic])
   (pintificate [:font-size] ["10px"])
   ))


(spit "resources/public/css/pint-12-30px.css"
      (apply str (map css (vec pints))))

;; example structure
(def post-tile
  [:div.db.fw100 "My Post"
   [:div.dib "pants"]
   ]
  )

(def classes
  "Get classes from hiccup keyword"
  (comp
   (partial map keyword)
   (partial re-seq #"\.[^\.]+")
   str))


(defn collate-styles [hiccup-key]
  (apply merge
         (map (partial get pints)
              (classes hiccup-key))))

(defn traditional-css
  "Generate traditional nested css structure from markup."
  [markup]
  (into
   [(first markup)
    (collate-styles (first markup))]
   (map traditional-css (filter vector? (rest markup)))))

(css (traditional-css post-tile))
(traditional-css post-tile)

