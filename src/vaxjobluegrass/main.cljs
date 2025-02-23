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
   |  \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF  \\
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
     :tablature  [
                  ;; {:instrument "mandolin"
                  ;;  :label      "Mandolin (%T)"
                  ;;  :tuning     ["G,", "D", "A", "e"]
                  ;;  :capo       0}
                  ;; {:instrument "guitar"
                  ;;  :label      "Guitar (%T)"
                  ;;  :tuning     ["D,", "A,", "D", "G", "A", "d"]
                  ;;  :capo       0}
                  ]})))

(def cherokee-abc
"
X: 1
T: Cherokee Shuffle
M: 4/4
L: 1/8
K: A
T: A part
EF |: \"A\" A2 Ac BAFE  | ABAF E2 FG        | AGAB cdeA        | \"F#m\" effa f2 ag |
   |  \"D\" fefg  aAaf  | \"A\" efed cBAB   | \"E\" cBAc BAFE   \\
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

;; [M:2/4] http://www.lesession.co.uk/abc/abc_notation.htm

(def jerusalem-abc
"
X: 1
T: Jerusalem Ridge
M: 4/4
L: 1/8
K: Am
T: A part
|: \"Am\"  A,B,CD E2 EF | EDCE DCEC | A,B,CD EGAG | \"E\" EDCE DCEC |
|  \"Am\"  A,B,CD E2 EF | EDCE DCEC | A,B,CD EGAG | \"E\" EDCB, \"Am\" A,4 :|
T: B part
|: \"Am\"  [EA]2 AA A2 AG | EGAB cdcA | EA AA A2 AB | \"E\" cdee e4          |
|  \"Am\"  [EA]2 AA A2 AG | EGAB cdcA | EGAB cdcA   | \"E\" GEDC \"Am\" A,4 :|
T: C part
| [M:2/4] A,2 (3AGF   | [M:4/4] \"Am\" E2 E2 EE^FE | \"D\" D2 D2 D^FED | \"Am\" CC C2 \"E\" B,B, B,2 | \"Am\" A,2 A,2 A,2 (3AGF |
| \"Am\"  E2 E2 EE^FE | \"D\" DA,D^F DFED | \"Am\" CE C2 \"E\" B,CB,2 | \"Am\" A,2 A,A, A,4 |
T: D part
|: \"Am\" e2 a2 a2 g  | a2 b2 c'4   | \"C\" egga g2 ge | gc'ag edcd |
|  \"Am\" eaag a2 ab  | c'abg  aged | e2 eg edcd | \"E\" edc A4 |
|  \"Am\" A2 Ac AGED  | E2 EG EDCD  | [M:2/4] EC DC | [M:4/4] C2 C2 C2 (3DCB, | A,2 A,2 A,4 :|
")

(defn jerusalem-score []
  (render-abc
    "jerusalem-score"
    jerusalem-abc
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
  (cherokee-score)
  (jerusalem-score))
