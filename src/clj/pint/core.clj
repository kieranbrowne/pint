(ns pint.core
  (:require [pint.grid :as grid]
            [pint.utils :as utils
             :refer [unit-range pintificate home-brew]]
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

; values

(def gutter ".8rem")
(def baseline "1rem")

; combinations

(def pints
  "Collate all pints"
  (merge
   (pintificate [:display] [:inline-block :block :flex])
   (pintificate [:cursor] [:pointer])
   (pintificate [:box-sizing] [:content-box :border-box])
   (pintificate [:flex-grow :flex-shrink] [1 0])
   (pintificate [:flex-wrap] [:wrap :no-wrap :wrap-reverse])
   (pintificate [:justify-content]
                [:flex-start :flex-end :center :space-between :space-around])
   (pintificate [:font-weight] (range 100 1000 100))
   (pintificate [:font-weight] [:bold :normal])
   (pintificate [:font-style] [:italic])
   (pintificate [:font-size] (unit-range "px" 6 73 6))
   (pintificate [:font-size] (unit-range "rem" 1 8 1))
   (pintificate [:border-radius] (unit-range "px" 0 11))
   (pintificate [:appearance :border] [:none])
   (pintificate [:width :max-width :height :max-height :line-height :margin :padding-top :font-size]
                standard-values)
   (pintificate [:color :background-color] [:black :grey :white])
   (pintificate [:text-align] [:left :center :right :justify])
   (pintificate [:letter-spacing] (unit-range "px" -5/5 6/5 1/5))
   (home-brew #(str "w" % "c")
              #(assoc {} :width (grid/generate 12 gutter %))
              (range 1 13))
   (home-brew #(str "h" % "c")
              #(assoc {} :height (grid/generate 12 gutter %))
              (range 1 13))
   (home-brew #(str (utils/shorten %) "g")
              #(assoc {} % gutter)
              [:margin-left :margin-right :margin-bottom :margin-top :padding-left :padding-right :padding-top :padding-bottom])
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
