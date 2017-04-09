(ns pint.library
  (:require [pint.style :as style]))

(def library
  {"tiles"
   {
    "basic-post"
    {:title "Basic Post Tile"
    :structure
    [:div.post-tile.w3c "test component"]}

    "pricing-table"
    {:title "Pricing Table"
     :structure
     [:div.post-tile.w3c.tac
      [:div.bcg.lh3rem.fwb.cw.fs.5rem "Premium Package"]
      [:div.bcw.lh4rem.fs6 "$30" [:span.fs1rem "/month"]]
      [:div.bcw.plg.prg
       [:a.fwb.bcg.plg.prg.pt6px.pb6px.cw.mbg "Buy Now"]
       ]
      ]}}
   "forms"
   {
    "login-form"
    {:title "Login Form"
     :structure
     [:div.w4c.h18px
      [:form.plg.ptg.prg.pbg {:action "" :method "get"}
       [:input.db.lh2rem.w100%.mbg.an.bn.fs5.plg.prg
        {:type "text" :style style/input
         :placeholder "Jane Citizen"}]
       [:input.db.lh2rem.w100%.mbg.an.bn.fs5.plg.prg
        {:type "email" :style style/input
         :placeholder "jane.citizen@example.com"
         }]
       [:button.db.an.fs4. "Login"]
       ]
      ]}}
   "content"
   {
    "paragraph"
    {:title "Paragraph"
     :structure
     [:div
      [:p.plg.prg.fs5.mw30em "Morbi quis commodo odio aenean sed adipiscing diam donec adipiscing tristique risus nec feugiat in fermentum posuere urna nec tincidunt. Duis convallis convallis tellus, id interdum velit laoreet id donec ultrices tincidunt arcu, non sodales neque sodales ut etiam sit."]
      [:p.plg.prg.fs5 "Nunc lobortis mattis aliquam faucibus purus in massa tempor nec feugiat nisl pretium fusce id velit ut tortor pretium viverra suspendisse potenti nullam ac tortor vitae purus faucibus."]]
     }
    }
   })
