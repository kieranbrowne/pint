(ns pint.grid)

(defn generate
  [columns gutter n final]
  (str
    "calc("
    "(96.3 * 1vw - " gutter "*13) /" columns
    " * " n " + "
    gutter " * " (if final (- n 1) n) ")"
    ))

