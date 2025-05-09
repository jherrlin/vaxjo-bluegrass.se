(ns vaxjobluegrass.main
  (:require ["abcjs" :as abcjs]
            [reagent.dom.client :as rdc]
            [clojure.edn :as edn]
            [reagent.core :as r]))

;;
;; State
;;

(defonce state (r/atom nil))
(defonce show-abc-state (r/atom {}))
(defonce visualObjs (r/atom {}))
(defonce midiBuffer (atom nil))

(def audio-context
  (or js/window.AudioContext
      js/window.webkitAudioContext
      js/navigator.mozAudioContext
      js/navigator.msAudioContext))

(defn set-audio-context! [audio-context]
  (set! js/window.AudioContext audio-context))

(defn play-sound! [id]
  (let [_             (set-audio-context! audio-context)
        audio-context (new js/window.AudioContext)
        visualObj     (get @visualObjs id)]
    (when visualObj
      (-> audio-context
          (.resume)
          (.then (fn []
                   (let [_ (swap! midiBuffer (fn [buffer]
                                               (try
                                                 (.stop buffer)
                                                 (catch js/Error _))
                                               (new abcjs/synth.CreateSynth)))]
                     (-> (.init @midiBuffer (clj->js {:visualObj              visualObj
                                                      :audioContext           audio-context
                                                      :millisecondsPerMeasure (.millisecondsPerMeasure visualObj)
                                                      :options                {:swing false}}))
                         (.then (fn [_]
                                  (.prime @midiBuffer)))
                         (.then (fn [_]
                                  (.start @midiBuffer)
                                  (js/Promise.resolve)))
                         (.catch (fn [e]
                                   (js/console.log "error:" e)))))))))))

(defn stop-sound! []
  (let [buffer @midiBuffer]
    (when buffer (.stop buffer))))

(comment
  (play-sound! :whiskey)
  (stop-sound!)
  :-)

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

(defn render-abc [{:keys [dom-id abc ops id]}]
  (let [visualObj (abcjs/renderAbc dom-id abc ops)]
    (swap! visualObjs assoc id (first visualObj))))

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
   {:abc    whiskey-abc
    :dom-id "whiskey-score"
    :id     :whiskey
    :ops    (clj->js
             (cond-> {:responsive "resize"}
               tab (assoc :tablature [tab])))}))

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
   {:abc    cherokee-abc
    :dom-id "cherokee-score"
    :id     :cherokee
    :ops    (clj->js
             (cond-> {:responsive "resize"}
               tab (assoc :tablature [tab])))}))

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
   {:abc    jerusalem-abc
    :dom-id "jerusalem-score"
    :id     :jerusalem
    :ops    (clj->js
             (cond-> {:responsive "resize"}
               tab (assoc :tablature [tab])))}))

(def liberty-abc
  "X: 1
T: Liberty
M: 4/4
L: 1/8
K: D
P: A
ag |: \"D\" f2 A2 f2 A2 |  fefg fedf  | \"G\" g2 B2 g2 B2 | gfga gfef |
   |  \"D\" f2 A2 f2 A2 |  fefg fdef  | \"G\" (f/2g/2)fed \"A\" cABc |
   |1 \"D\" dBAF D2 ag :|2 \"D\" dBAF D2 FG ||
P: B
   |: \"D\" A2 AB AFEF  | DFAd f2 d2 | A2 AB AFDF | \"A\" GD F2 E2 FG |
   |  \"D\" A2 AB AFEF  | DFAd f2ef  | \"A\" (f/2g/2)fed cABc |
   |1 \"D\" dBAF D2 FG :|2 \"D\" dBAF D4 ||")

(defn liberty-score [tab]
  (render-abc
   {:abc    liberty-abc
    :dom-id "liberty-score"
    :id     :liberty
    :ops    (clj->js
             (cond-> {:responsive "resize"}
               tab (assoc :tablature [tab])))}))

(defn scores [state']
  (whiskey-score (->tablature state'))
  (cherokee-score (->tablature state'))
  (jerusalem-score (->tablature state'))
  (liberty-score (->tablature state')))

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

        [:div {:id "whiskey-score"}]

        [:div {:style {:display "flex"}}
         [:button {:style    {:margin-right "1em"}
                   :on-click (fn [_] (play-sound! :whiskey))} "Play"]
         [:button {:on-click (fn [_] (stop-sound!))} "Stop"]]]

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

        [:div {:id "cherokee-score"}]
        [:div {:style {:display "flex"}}
         [:button {:style    {:margin-right "1em"}
                   :on-click (fn [_] (play-sound! :cherokee))} "Play"]
         [:button {:on-click (fn [_] (stop-sound!))} "Stop"]]]

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
          [:pre jerusalem-abc])

        [:div {:id "jerusalem-score"}]

        [:div {:style {:display "flex"}}
         [:button {:style    {:margin-right "1em"}
                   :on-click (fn [_] (play-sound! :jerusalem))} "Play"]
         [:button {:on-click (fn [_] (stop-sound!))} "Stop"]]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Liberty"]
        [:p "Struktur: AABB"]
        [:br]
        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=VznnaupyRzQ"} "Liberty"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=PkT7W8BTc9I"} "Liberty 80 BPM - bluegrass backing track"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=vIisdof5do4"} "Liberty 90 BPM - bluegrass backing track"]]
         [:li [:a {:href "https://www.youtube.com/watch?v=vFbSVYsZ4EE"} "Liberty 100 BPM - bluegrass backing track"]]
         [:li
          [:button {:on-click #(swap! show-abc-state update :liberty not)} "ABC notation"]]]

        (when (get @show-abc-state :liberty)
          [:pre liberty-abc])

        [:div {:id "liberty-score"}]

        [:div {:style {:display "flex"}}
         [:button {:style    {:margin-right "1em"}
                   :on-click (fn [_] (play-sound! :liberty))} "Play"]
         [:button {:on-click (fn [_] (stop-sound!))} "Stop"]]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Blue Ridge Cabin Home"]

        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=gcZN9x4q4kg"} "Flatt & Scruggs - Blue Ridge Cabin Home"]]]

        [:br]

        [:pre
         "
| G | C | D7 | G |

G                                 C
There's a well beaten path in the old mountainside
        D7                    G
Where I wandered when I was a lad
                            C
And I wandered alone to the place I call home
         D7                   G
In those Blue Ridge hills far away

                             C
Oh I love those hills of old Virginia
           D7                     G
From those Blue Ridge hills I did roam
                                    C
When I die won't you bury me on the mountain
    D7                               G
Far away near my Blue Ridge mountain home


- [Solo]


Now my thoughts wander back to that ramshackle shack
In those Blue Ridge hills far away
Where my mother and dad were laid there to rest
They are sleeping in peace together there


- [Chorus] Oh I love those hills of old Virginia...

- [Solo]


I return to that old cabin home with the sigh
I've been longing for days gone by
When I die won't you bury me on that old mountain side
Make my resting place upon the hills so high


- [Chorus] Oh I love those hills of old Virginia...

- [Solo]

- [Chorus] Oh I love those hills of old Virginia...
"]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Katy Daley"]

        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=5CMj2yd-W1E"} "Katy Daley"]]]

        [:br]

        [:pre
         "
| G | D |


G
With her old man she came from Tipperary
                          D
In the pioneering days of '42
                                  D7
Her old man was shot in Tombstone City
                                        G
For the making of his good old mountain dew

   G
Oh Come on down the mountain Katy Daley
                               D
Come on down the mountain Katy do
                               D7
Can't you hear us calling Katy Daley
                                        G
We want to drink your good old mountain dew

- [Solo]

Wake up and pay attention Katy Daley
For I'm the judge that's gonna sentence you
All the boys in court have drunk your whiskey
To tell the truth I like a little too

- [Chorus] Oh Come on down the mountain Katy Daley...

- [Solo]

So to the jail they took poor Katy Daley
And pretty soon the gates were open wide
Angels came for poor old Katy Daley
Took her far across the great divide

- [Chorus] Oh Come on down the mountain Katy Daley...

- [Solo]

- [Chorus] Oh Come on down the mountain Katy Daley..."]]

       [:br] [:br] [:hr] [:br] [:br]

       [:div
        [:h2 "Wagon Wheel"]

        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=u6TxewbL9BU"} "Old Crow Medicine Show - Wagon Wheel"]]]

        [:br]

        [:pre
         "
| A | E | F#m | D |
| A | E | D   | D |

A                         E
Heading down south to the land of the pines
    F#m                  D
I'm thumbing my way into North Caroline
A                       E                 D
Staring up the road and pray to God I see headlights
  A                         E
I made it down the coast in seventeen hours
F#m                     D
Picking me a bouquet of dogwood flowers
          A                         E           D
And I'm a-hopin' for Raleigh, I can see my baby tonight

   A                    E
So rock me momma like a wagon wheel
F#m               D
Rock me momma any way you feel
A       E   D
Hey,  mamma rock me
A                      E
Rock me momma like the wind and the rain
F#m                  D
Rock me momma like a south bound train
A       E   D
Hey,  mamma rock me

- [Solo]

Running from the cold up in New England
I was born to be a fiddler in an old time string band
My baby plays a guitar, I pick a banjo now
Oh, north country winters keep a-getting me down
Lost my money playing poker so I had to leave town
But I ain't turning back to living that old life no more

- [Chorous] So rock me momma like a wagon wheel...

- [Solo]

Walkin' to the south out of Roanoke
I caught a trucker out of Philly, had a nice long toke
But he's a heading west from the Cumberland gap
To Johnson City, Tennessee
I gotta get a move on before the sun
I hear my baby calling my name and I know that she's the only one
And if I die in Raleigh at least I will die free

- [Chorous] So rock me momma like a wagon wheel...

- [Solo]

- [Chorous] So rock me momma like a wagon wheel..."]]



       [:div
        [:h2 "Shady Grove"]

        [:ul
         [:li [:a {:href "https://www.youtube.com/watch?v=I0f-dCMSVKQ"} "Doc Watson - Shady Grove"]]]

        [:br]

        [:pre
         "| DM | C | DM | F | C | Dm |


Dm           C              Dm
Shady Grove, my little love Shady Grove I say

F            C                               Dm
Shady Grove, my little love. I'm bound to go away.


Cheeks as red as a blooming rose and eyes are the prettiest brown.
She's the darling of my heart, sweetest little girl in town

- [Chorous] Shady Grove, my little love...

- [Solo]

I wish I had a big fine horse and corn to feed him on.
And Shady Grove to stay at home and feed him while I'm gone.

- [Chorous] Shady Grove, my little love...

- [Solo]

Went to see my Shady Grove, she was standing in the door.
Her shoes and stockin's in her hand and her little bare feet on the floor.

- [Chorous] Shady Grove, my little love...

- [Solo]

When I was a little boy I wanted a Barlow knife.
And now I want little Shady Grove to say she'll be my wife.

- [Chorous] Shady Grove, my little love...

- [Solo]

Kiss from pretty little Shady Grove is sweet as brandy wine.
And there ain't no girl in this old world that's prettier than mine.

- [Chorous] Shady Grove, my little love...

- [Solo]

- [Chorous] Shady Grove, my little love..."]]])}))

(defonce root-container
  (rdc/create-root (js/document.getElementById "app")))

(defn mount-ui
  []
  (rdc/render root-container [ui]))

(defn ^:dev/after-load init []
  (mount-ui))
