(ns vaxjobluegrass.main
  (:require [reagent.dom.client :as rdc]
            ["abcjs" :as abcjs]))

(def whiskey
"
X: 1
T: Whiskey Before Breakfast
M: 4/4
L: 1/8
K: D
T: A part
DD |: \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF | \"A\" EDEF EDCE |
   |  \"D\" DEFG A2 FG | ABAG FDEF  | \"G\" GABG \"D\" F2 AF |1 \"A\" EDEF \"D\" D2 DD :|2 \"A\" EDEF \"D\" D2 FE ||
T: B part
   |:\"D\" DFAc d2 d2  | cdde fe d2 | \"Em\" eeef e2 ef      | \"A\" gfed cABc |
   | \"D\" defd \"A\" c2 ec | \"G\" BABc \"D\" BAFD | \"G\" GABG \"D\" F2 AF \" \\
   |1 \"A\" EDEF \"D\" D2 FE :|2 \"A\" EDEF \"D\" D4 || ")


(defn whiskey-score []
  ((get (js->clj abcjs) "renderAbc") "whiskey-score" whiskey (clj->js {:responsive "resize"
                                                                       ;; :tablature [{:instrument "guitar"
                                                                       ;;              :label "Guitar (%T)"
                                                                       ;;              :tuning ["D,", "A,", "D", "G", "A", "d"]
                                                                       ;;              :capo 0
                                                                       ;;              }]
                                                                       })))

;; (defn ui []
;;   [:div {:id "abc-root"}
;;    [:h2 "ClojureScript"]])

;; (defonce root-container
;;   (rdc/create-root (js/document.getElementById "app")))

;; (defn mount-ui
;;   []
;;   (rdc/render root-container [ui]))

(defn ^:dev/after-load init []
  ;; (mount-ui)
  (whiskey-score))


(comment


  :-)
