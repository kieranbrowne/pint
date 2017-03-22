(ns pint.grid)

(defn generate
  [columns gutter n]
  (str
    "calc("
    "(100vw - " gutter "*" (+ 1 (/ columns n)) ") / (" (/ columns n) "))"
    ))

