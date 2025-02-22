(ns vaxjobluegrass.main
  (:require ["abcjs" :as abcjs]))


(def render-abc (get (js->clj abcjs) "renderAbc"))

(def whiskey-abc
  "
X: 1
T: Whiskey Before Breakfast
M: 4/4
L: 1/8
K: D
T: A part
DD |: \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF | \"A\" EDEF EDCE |
   |  \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF | \\
   |1 \"A\" EDEF \"D\" D2 DD :|2 \"A\" EDEF \"D\" D2 FE ||
T: B part
   |:\"D\" DFAc d2 d2  | cdde fe d2 | \"Em\" eeef e2 ef      | \"A\" gfed cABc |
   | \"D\" defd \"A\" c2 ec | \"G\" BABc \"D\" BAFD | \"G\" GABG \"D\" F2 AF \" \\
   |1 \"A\" EDEF \"D\" D2 FE :|2 \"A\" EDEF \"D\" D4 || ")

(defn whiskey-score []
  (render-abc
   "whiskey-score"
   whiskey-abc
   (clj->js
    {:responsive "resize"
      ;; :tablature [{:instrument "guitar"
      ;;              :label "Guitar (%T)"
      ;;              :tuning ["D,", "A,", "D", "G", "A", "d"]
      ;;              :capo 0
      ;;              }]
     })))

(def cherokee-abc
"X: 1
T: Cherokee Shuffle
M: 4/4
L: 1/8
K: A
T: A part
EF |: \"A\" A2 Ac BAFE  | ABAF E2 FG        | AGAB cdeA        | \"F#m\" effa f2 ag |
   |  \"D\" fefg  aAaf  | \"A\" efed cBAB   | \"E\" cBAc BAFE  | \\
   |1 \"A\" AAAB A2 EF :|2 \"A\" AAAB A2 ag ||
T: B part
   |: \"D\" fefg aAaf    | \"A\" efed cAae   | \"D\" fefg aA e2  | \"A\" bc'c'd' c'eag | \"D\" fefg abaf |
   |  \"A\" efed cBAG    | A2 AB cd e2       | \"F#m\" effa fAaf | \"E\" efed cABc | \\
   |1 \"A\" A2 AB A2 ag :|2 \"A\" A2 AB A4 ||")

(defn cherokee-score []
  (render-abc
    "cherokee-score"
    cherokee-abc
    (clj->js
      {:responsive "resize"
       ;; :tablature [{:instrument "guitar"
       ;;              :label "Guitar (%T)"
       ;;              :tuning ["D,", "A,", "D", "G", "A", "d"]
       ;;              :capo 0
       ;;              }]
       })))


(defn ^:dev/after-load init []
  ;; (mount-ui)
  (whiskey-score)
  (cherokee-score))
