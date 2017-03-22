(ns pint.utils
  (:require [clojure.math.combinatorics :refer [cartesian-product]]))

(defn unit [unit value]
  (str value unit))

(defn unit-range [u & others]
  (map (partial unit u) (apply range others)))

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


(def str-to-class
  (comp keyword (partial str ".")))

(def pint-class
  "Convert cssmap into pint microclass"
  (comp str-to-class shorten))

(defn class-name
  "TODO: this will make h,v and a microclasses for left, right,
  top and bottom classes."
  [propval]
  (let [property (first (keys propval))
        value (first (vals propval))]
    [(if (re-find #"(-left)|(-right)" (str property)) :.mh :empty)
     (if (re-find #"(-top)|(-bottom)" (str property)) :.mv :empty)
     (if (re-find #"(-left)|(-right)|(-top)|(-bottom)" (str property)) :.ma)
     propval]
    )
  )

(defn pintificate
  "Auto-generate microclass for every combination of props and values."
  [props values]
  (apply merge
         (map
          (fn [x] {(pint-class x) x})
          (comb props values))))

(defn home-brew
  "Brewing pints on hard mode; a function"
  [name-fn value-fn inputs]
  (apply merge
         (map (fn [x] {(str-to-class (name-fn x)) (value-fn x)})
              inputs)))

(def classes
  "Get classes from hiccup keyword"
  (comp
   (partial map keyword)
   (partial re-seq #"\.[^\.]+")
   str))


(defn collate-styles [pints hiccup-key]
  (apply merge
         (map (partial get pints)
              (classes hiccup-key))))

(defn traditional-css
  "Generate traditional nested css structure from markup."
  [pints markup]
  (into
   [(first markup)
    (collate-styles pints (first markup))]
   (map #(traditional-css pints %) (filter vector? (rest markup)))))
