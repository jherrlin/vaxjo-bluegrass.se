(ns vaxjobluegrass.main
  (:require ["abcjs" :as abcjs]
            [reagent.dom.client :as rdc]
            [clojure.edn :as edn]
            [reagent.core :as r]))

(def state (r/atom nil))
(def show-abc-state (r/atom {}))


(def instruments
  {"guitar"        {:instrument "guitar"
                    :label      "Guitar (%T)"
                    :tuning     ["E,", "A,", "D", "G", "B", "e"] ;; E2 A2 D3 G3 B3 E4
                    :capo       0}
   "guitar-dadgad" {:instrument "guitar"
                    :label      "Guitar (%T)"
                    :tuning     ["D,", "A,", "D", "G", "A", "d"]
                    :capo       0}
   "mandolin"      {:instrument "mandolin"
                    :label      "Mandolin (%T)"
                    :tuning     ["G,", "D", "A", "e"]
                    :capo       0}
   "mandola"       {:instrument "mandolin"
                    :label      "Mandola (%T)"
                    :tuning     ["C,", "G,", "D", "A"]
                    :capo       0}})

(defn ->tablature [s]
  (when s
    (get instruments s)))

(def render-abc (get (js->clj abcjs) "renderAbc"))

(def whiskey-abc
  "
X: 1
T: Whiskey Before Breakfast
M: 4/4
L: 1/8
K: D
P: A
DD |: \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF | \"A\" EDEF EDCE |
   |  \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF  \\
   |1 \"A\" EDEF \"D\" D2 DD :|2 \"A\" EDEF \"D\" D2 FE ||
P: B
   |:\"D\" DFAc d2 d2  | cdde fe d2 | \"Em\" eeef e2 ef      | \"A\" gfed cABc |
   | \"D\" defd \"A\" c2 ec | \"G\" BABc \"D\" BAFD | \"G\" GABG \"D\" F2 AF \" \\
   |1 \"A\" EDEF \"D\" D2 FE :|2 \"A\" EDEF \"D\" D4 || ")

(defn whiskey-score [tab]
  (render-abc
   "whiskey-score"
   whiskey-abc
   (clj->js
     (cond-> {:responsive "resize"}
      tab (assoc :tablature [tab])))))

(def cherokee-abc
"
X: 1
T: Cherokee Shuffle
M: 4/4
L: 1/8
K: A
P: A
EF |: \"A\" A2 Ac BAFE  | ABAF E2 FG        | AGAB cdeA        | \"F#m\" effa f2 ag |
   |  \"D\" fefg  aAaf  | \"A\" efed cBAB   | \"E\" cBAc BAFE   \\
   |1 \"A\" AAAB A2 EF :|2 \"A\" AAAB A2 ag ||
P: B
   |: \"D\" fefg aAaf    | \"A\" efed cAag   | \"D\" fefg aA e2  | \"A\" bc'c'd' c'eag | \"D\" fefg abaf |
   |  \"A\" efed cBAG    | A2 AB cd e2       | \"F#m\" effa fAaf | \"E\" efed cABc | \\
   |1 \"A\" A2 AB A2 ag :|2 \"A\" A2 AB A4 ||")

(defn cherokee-score [tab]
  (render-abc
    "cherokee-score"
    cherokee-abc
    (clj->js
     (cond-> {:responsive "resize"}
      tab (assoc :tablature [tab])))))

;; [M:2/4] http://www.lesession.co.uk/abc/abc_notation.htm

(def jerusalem-abc
"
X: 1
T: Jerusalem Ridge
M: 4/4
L: 1/8
K: Am
P: A
|: \"Am\"  A,B,CD E2 EF | EDCE DCEC | A,B,CD EGAG | \"E\" EDCE DCEC |
|  \"Am\"  A,B,CD E2 EF | EDCE DCEC | A,B,CD EGAG | \"E\" EDCB, \"Am\" A,4 :|
P: B
|: \"Am\"  [EA]2 AA A2 AG | EGAB cdcA | EA AA A2 AB | \"E\" cdee e4          |
|  \"Am\"  [EA]2 AA A2 AG | EGAB cdcA | EGAB cdcA   | \"E\" GEDC \"Am\" A,4 :|
P: C
| [M:2/4] A,2 (3AGF   | [M:4/4] \"Am\" E2 E2 EE^FE | \"D\" D2 D2 D^FED | \"Am\" CC C2 \"E\" B,B, B,2 | \"Am\" A,2 A,2 A,2 (3AGF |
| \"Am\"  E2 E2 EE^FE | \"D\" DA,D^F DFED | \"Am\" CE C2 \"E\" B,CB,2 | \"Am\" A,2 A,A, A,4 |
P: D
|: \"Am\" e2 a2 a2 g  | a2 b2 c'4   | \"C\" egga g2 ge | gc'ag edcd |
|  \"Am\" eaag a2 ab  | c'abg  aged | e2 eg edcd | \"E\" edc A4 |
|  \"Am\" A2 Ac AGED  | E2 EG EDCD  | [M:2/4] EC DC | [M:4/4] A,2 A,2 A,2 (3DCB, | A,2 A,2 A,4 :|
")

(defn jerusalem-score [tab]
  (render-abc
   "jerusalem-score"
   jerusalem-abc
   (clj->js
    (cond-> {:responsive "resize"}
      tab (assoc :tablature [tab])))))

(defn scores [state']
  (whiskey-score (->tablature state'))
  (cherokee-score (->tablature state'))
  (jerusalem-score (->tablature state')))

(defn ui []
  (r/create-class
   {:component-did-mount  #(scores @state)
    :component-did-update #(scores @state)

       ;; name your component for inclusion in error messages
    :display-name "scores"

       ;; note the keyword for this method
    :reagent-render
    (fn []
      [:div
       [:div {:style {:display "flex"}}
        [:p "Välj ett instrument om du vill ha tabbar till noterna: "]
        [:select {:value     (prn-str (or @state "Select instrument"))
                  :on-change (fn [evt]
                               (let [value (-> evt .-target .-value edn/read-string)]
                                 (reset! state (if (= value "Select instrument")
                                                 nil
                                                 value))))}
         (for [t (concat ["Välj instrument"] (keys instruments))]
           ^{:key t}
           [:option {:value (prn-str t)} t])]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Whiskey before breakfast"]
        [:p "Struktur: AABB"]
        [:br]
        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=VtxdaAui4tw"} "Whiskey before breakfast"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=Oyc2XOabo38"} "Backing track 80bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=0RSmygzC4cU"} "Backing track 90bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=DqbvvnEQWKU"} "Backing track 100bpm"]]
         [:li
          [:button {:on-click #(swap! show-abc-state update :whiskey not)} "ABC notation"]]]

        (when (get @show-abc-state :whiskey)
          [:pre whiskey-abc])

        [:div {:id "whiskey-score"}]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Cherokee Shuffle"]
        [:p "Struktur: AABB"]
        [:br]
        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=Pdq6Nw1Qc8U"} "Bryan Sutton and Mike Marshall - Cherokee Shuffle"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=EJyLFJmFaD8"} "Osborne Brothers - Cherokee Shuffle"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=d6mKaf6fYNU"} "Backing track 80bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=b54RbwJre9U"} "Backing track 90bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=L7kFikX75a4"} "Backing track 100bpm"]]
         [:li
          [:button {:on-click #(swap! show-abc-state update :cherokee not)} "ABC notation"]]]

        (when (get @show-abc-state :cherokee)
          [:pre cherokee-abc])

        [:div {:id "cherokee-score"}]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Jerusalem Ridge"]
        [:p "Struktur: AABBCDD"]
        [:br]
        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=jgV41BD9Fvw"} "Bill Monroe & Kenny Baker - Jerusalem Ridge"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=_Kx__tN5FhI"} "Tony Rice - Jerusalem Ridge"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=KTmLQlB1PSI"} "Chris Thile & Tim O'Brien - Jerusalem Ridge - Grey Fox 2011"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=bE4cTmewl4Y"} "Backing track 80 bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=eJJb101E3Q8"} "Backing track 90bpm"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=Kylwml222kc"} "Backing track 100bpm"]]
         [:li
          [:button {:on-click #(swap! show-abc-state update :jerusalem not)} "ABC notation"]]]

        (when (get @show-abc-state :jerusalem)
          [:pre cherokee-abc])

        [:div {:id "jerusalem-score"}]]])}))

(defonce root-container
  (rdc/create-root (js/document.getElementById "app")))

(defn mount-ui
  []
  (rdc/render root-container [ui]))

(defn ^:dev/after-load init []
  (mount-ui))
