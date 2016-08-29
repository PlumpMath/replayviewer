(ns replayviewer.html
  (:require [reagent.core :as reagent]
            [replayviewer.css :as css]))

(defn p [e]
  (js/console.table (clj->js e)))

;; {:minion [:name :attack :health]}
(defn minion [minion]
  [:div.minion css/minion
    [:span (:name minion)]
    [:span css/minion-attack (:attack minion)]
    [:span css/minion-health (- (:health minion) (:damage minion))]])

;; {:secret [:name]}
(defn secret [secret]
  [:span.secret css/secret (:name secret)])

;; {:weapon [:name :attack :durability]}
(defn weapon [weapon]
  [:div.weapon css/weapon
    [:span (:name weapon)]
    [:span (:attack weapon)]
    [:span (:durability weapon)]])

;; {:hero [:name :health :armor]}
(defn hero [hero]
  [:div.hero css/hero
    [:span (:name hero)]
    [:span (:health hero)
    [:span (:armor hero)]]])

;; {:power [:name :cost :used]}
(defn power [power]
  [:div.power css/power
    [:span (:name power)]
    [:span (:cost power)]
    [:span (:used power)]])

(defn full-image [path]
  [:div {:style {:position "absolute"}}
    [:img {:src path}]])

(defn full-image-at-cursor [e]
  (js/console.log (-> e .-target .-src))
  (reagent/render (full-image (-> e .-target .-src))))

;; {:card [:name :cost]}
(defn card [card]
  [:div.card css/card
    #_[:span (:name card)]
    #_[:span (:cost card)]
    [:img {:src (:img (:data card))
           :width "80px"
           :on-mouseOver full-image-at-cursor}]])

;; {:deck [:count]}
(defn deck [deck]
  [:div.deck css/deck
    [:span (count deck)]])

(defn board [board]
  [:div.board css/board
    (for [m board]
      ^{:key m} [minion m])])

(defn secrets [secrets]
  [:div.secrets css/secrets
    (for [s secrets]
      ^{:key s} [secret s])])

(defn hand [hand]
  [:div.hand css/hand
    (for [c hand]
      ^{:key c} [card c])])

(defn game [state]
  (let [game (:game state)
        swapped? (:players-swapped? state)
        players (if swapped? (reverse (:players game)) (:players game))]
    (p (->> players first :hand (filter :data) (map :data)))
    [:div#game
      (let [player (first players)]
        [:div#top-player        css/top-player
          [:div#top-board       css/top-board       (board    (:board   player))]
          [:div#top-secrets     css/top-secrets     (secrets  (:secrets player))]
          [:div#top-weapon      css/top-weapon      (weapon   (:weapon  player))]
          [:div#top-hero        css/top-hero        (hero     (:hero    player))]
          [:div#top-power       css/top-power       (power    (:power   player))]
          [:div#top-deck        css/top-deck        (deck     (:deck    player))]
          [:div#top-hand        css/top-hand        (hand     (:hand    player))]])
      (let [player (second players)]
        [:div#bottom-player     css/bottom-player
          [:div#bottom-board    css/bottom-board    (board    (:board   player))]
          [:div#bottom-secrets  css/bottom-secrets  (secrets  (:secrets player))]
          [:div#bottom-weapon   css/bottom-weapon   (weapon   (:weapon  player))]
          [:div#bottom-hero     css/bottom-hero     (hero     (:hero    player))]
          [:div#bottom-power    css/bottom-power    (power    (:power   player))]
          [:div#bottom-deck     css/bottom-deck     (deck     (:deck    player))]
          [:div#bottom-hand     css/bottom-hand     (hand     (:hand    player))]])]))