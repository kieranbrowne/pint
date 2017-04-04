(ns pint.library)

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
      ]}
    }
   })
