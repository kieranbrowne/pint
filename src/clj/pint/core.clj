(ns pint.core
  (:require [pint.grid :as grid]
            [pint.utils :as utils :refer [unit shorten]]
            [garden.core :refer [css]]
            ))

(map (partial unit "px") (range 0 10))

(defn class-name [propval]
  (let [property (first (keys propval))
        value (first (vals propval))]
      [(if (re-find #"(-left)|(-right)" (str property)) :.mh :empty)
      (if (re-find #"(-top)|(-bottom)" (str property)) :.mv :empty)
      (if (re-find #"(-left)|(-right)|(-top)|(-bottom)" (str property)) :.ma)
      propval]
    )
  )

((partial (complement =) :empty) :sdf)

(css (class-name {:margin-left "10%"}))

(def standard-values
  (concat 
    [0]
    (map (partial unit "%") (range 10 110 10))
    (map (partial unit "%") (range 33.33 99.99 33.33))
    (map (partial unit "px") (range 0 10))
    (map (partial unit "rem") (range 0 10))
    (map (partial unit "em") (range 0 10))
    (map #(grid/generate 12 "1rem" % true) (range 1 21))
    ))

(map (fn [value]
       {:margin-left value}) standard-values)

(shorten :width)

; font weights

(utils/cssify
  (utils/comb
    [[ "fw" :font-weight]] 
    (utils/zip (range 1 10) (range 100 1000 100))
    ))

; directions 

; standard
; vertical
; horizontal
; all

; padding

(utils/cssify
  (utils/comb
    [[ "pl" :padding-left]
     [ "pr" :padding-right]
     [ "pt" :padding-top]
     [ "pb" :padding-bottom]] 
    (utils/zip (range 1 10) (range 100 1000 100))
    ))



(defn shorten-value [value]
  ())


; properties (vals in rem)
[:.fw :font-weight]
(map #({:font-weight %}) (range 100 900 100))

(map (fn [x] {(first x) (last x)}) 
  (combo/cartesian-product [:.fw :font-weight] (range 100 900 100)))


;; probably just assume 12 cols

(def column "generate from gutter")

; values

(def gutter "12rem")
(def baseline "1rem")

; combinations

(map css [
          [:.test {:font-weight "3px"}]
          [:.ffas {:font-weight "3px"}]
          ])
