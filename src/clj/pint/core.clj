(ns pint.core
  (:require [pint.grid :as grid]
            [pint.utils :as utils :refer [unit-range pintificate]]
            [garden.core :refer [css]]
            [garden.compression :refer [compress-stylesheet]]
            ))



(def standard-values
  (concat 
    [0 "auto"]
    (unit-range "%" 10 110 10)
    (unit-range "%" 33.33 99.99 33.33)
    (unit-range "px" 0 10)
    (unit-range "rem" 0 10)
    (unit-range "em" 0 10)
    (unit-range "vw" 0 110 10)
    (unit-range "vh" 0 110 10)
    ))


; font weights

; directions 

; standard
; vertical
; horizontal
; all

; padding



; properties (vals in rem)

;; probably just assume 12 cols
(map #(grid/generate 12 "1rem" % true) (range 1 21))

; values

(def gutter "12rem")
(def baseline "1rem")

; combinations

(def pints
  "Collate all pints"
  (merge
   (pintificate [:display] [:inline-block :block :flex])
   (pintificate [:font-weight] (range 100 1000 100))
   (pintificate [:font-weight] [:bold :normal])
   (pintificate [:font-style] [:italic])
   (pintificate [:font-size] (unit-range "px" 6 73 6))
   (pintificate [:font-size] (unit-range "rem" 1 8 1))
   (pintificate [:border-radius] (unit-range "px" 0 11))
   (pintificate [:appearance :border] [:none])
   (pintificate [:width :max-width :height :max-height :margin :padding-top]
                standard-values)
   (pintificate [:color :background-color] [:black :grey :white])
   (pintificate [:text-align] [:left :center :right :justify])
   (pintificate [:letter-spacing] (unit-range "px" -5/5 6/5 1/5))
   ))



;; write pint file
(spit "resources/public/css/pint-12-30px.css"
      (compress-stylesheet
       (apply str (map (comp #(str % " ") css) (vec pints)))))


;; example structure
(def post-tile
  [:div.db.fw100 "My Post"
   [:div.dib "pants"
    [:p.df "Lorem ipsum"]]])

(utils/traditional-css pints post-tile)
