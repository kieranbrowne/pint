(ns pint.core
  (:require [pint.grid :as grid]
            [pint.utils :as utils :refer [unit-range pintificate]]
            [garden.core :refer [css]]
            ))



(def standard-values
  (concat 
    [0 "auto"]
    (unit-range "%" 10 110 10)
    (unit-range "%" 33.33 99.99 33.33)
    (unit-range "px" 0 10)
    (unit-range "rem" 0 10)
    (unit-range "em" 0 10)
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
   (pintificate [:font-size] ["10px"])
   ))


;; write pint file
(spit "resources/public/css/pint-12-30px.css"
      (apply str (map css (vec pints))))

;; example structure
(def post-tile
  [:div.db.fw100 "My Post"
   [:div.dib "pants"]
   ]
  )

(utils/traditional-css post-tile)

(css
 (utils/traditional-css post-tile))
