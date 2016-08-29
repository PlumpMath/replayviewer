(ns replayviewer.css)

(def board
  {:style
    {:height "100%"
     :margin "0 auto"
     :display "inline-block"}})

(def minion
  {:style
    {:width "90px"
     :float "left"
     :border "1px solid #999"
     :margin "0 2px"
     :height "100%"
     :position "relative"}})

(def minion-attack
  {:style
    {:position "absolute"
     :bottom "0px"
     :left "0px"}})

(def minion-health
  {:style
    {:position "absolute"
     :bottom "0px"
     :right "0px"}})

(def secrets
  {:style
    {:border "1px solid black"
     :position "absolute"}})

(def secret
  {:style
    {:display "inherit"}})

(def weapon
  {:style
    {:border "1px solid black"
     :position "absolute"}})

(def hero
  {:style
    {:border "1px solid black"
     :position "absolute"}})

(def power
  {:style
    {:border "1px solid black"
     :position "absolute"}})

(def deck
  {:style
    {:border "1px solid black"
     :position "absolute"}})

(def card
  {:style
    {:border "1px solid black"
     :float "left"}})

(def hand
  {:style
    {}})

(def top-board
  {:style
    {:width "80%"
     :height "100px"
     :margin "0 auto"
     :background-color "#FDD"
     :padding "4px"
     :text-align "center"
     :bottom "0px"
     :left "86px"
     :position "absolute"}})

(def top-secrets
  {:style
    {:width "160px"
     :top "115px"
     :left "10px"
     :position "absolute"}})

(def top-weapon
  {:style
    {:width "80px"
     :left "250px"
     :top "150px"
     :position "absolute"}})

(def top-hero
  {:style
    {:left "360px"
     :top "150px"
     :position "absolute"}})

(def top-power
  {:style
    {:top "150px"
     :left "490px"
     :width "80px"
     :position "absolute"}})

(def top-deck
  {:style
    {:top "180px"
     :left "630px"
     :position "absolute"}})

(def top-hand
  {:style
    {:background-color "#EFE"
     :width "600px"
     :height "100px"
     :top "0px"
     :left "130px"
     :position "absolute"}})

(def bottom-board
  {:style
    {:width "80%"
     :height "100px"
     :margin "0 auto"
     :background-color "#FDD"
     :padding "4px"
     :text-align "center"}})

(def bottom-secrets
  {:style
    {:width "160px"
     :top "115px"
     :left "10px"
     :position "absolute"}})

(def bottom-weapon
  {:style
    {:width "80px"
     :left "250px"
     :top "150px"
     :position "absolute"}})

(def bottom-hero
  {:style
    {:left "360px"
     :top "150px"
     :position "absolute"}})

(def bottom-power
  {:style
    {:top "150px"
     :left "490px"
     :width "80px"
     :position "absolute"}})

(def bottom-deck
  {:style
    {:top "180px"
     :left "630px"
     :position "absolute"}})

(def bottom-hand
  {:style
    {:background-color "#EFE"
     :width "600px"
     :height "100px"
     :bottom "0px"
     :left "130px"
     :position "absolute"}})

(def top-player
  {:style
    {:width "900px"
     :border "1px solid black"
     :position "relative"
     :height "360px"}})

(def bottom-player
  {:style
    {:width "900px"
     :border "1px solid black"
     :position "relative"
     :height "360px"}})