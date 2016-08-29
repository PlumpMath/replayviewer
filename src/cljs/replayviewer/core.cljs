(ns replayviewer.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core]
              [replayviewer.css :as css]
              [replayviewer.html :as html]
              [replayviewer.converter :as converter]))

;; -------------------------
;; Views

;; A player is a map and is either on top or bottom



(defn player-from-data [n data]
  {:board (converter/board n data)
   :secrets (converter/secrets n data)
   :weapon (converter/weapon n data)
   :hero (converter/hero n data)
   :power (converter/power n data)
   :hand (converter/hand n data)
   :deck (converter/deck n data)})

(defn json->state [json cards]
  (let [data {:entities (:Data json) :cards cards}
        turn (:Turn json)]
    {:players
      [(player-from-data 1 data)
       (player-from-data 2 data)]}))

(def demo-player
  {:board []
   :secrets []
   :weapon {}
   :hero {}
   :power {}
   :hand []
   :deck []
   :name ""})

(def demo-game
  {:players [demo-player
             demo-player]
   :turn 0})

;; A game state is a map
(defonce state
  (atom
    {:game demo-game
     :players-swapped? false
     :value 0
     :replay [{}]
     :cards [{}]
     :cards-loaded false
     :card-popup {:visible false
                  :path ""}}))

(defn load-replay! [event]
  (let [file (js/JSON.parse (-> event .-target .-result))]
    (swap! state assoc-in [:replay] (js->clj file :keywordize-keys true))))

(defn read-file-input! [event]
  (let [files (-> event .-target .-files)
        file (.item files 0)
        reader (js/FileReader.)
        onload load-replay!]
    (aset reader "onload" onload)
    (.readAsText reader file)))

(defn get-cards-handler [[ok response]]
  (swap! state assoc-in [:cards]
    (->> response
      vals
      (mapcat (fn [card-list] (map (fn [card] {(:cardId card) card}) card-list)))
      (reduce merge {})))
  (swap! state assoc-in [:cards-loaded] true))

(defn get-cards []
  (ajax.core/ajax-request
    {:uri "/cards.json"
     :method :get
     :handler get-cards-handler
     :format (ajax.core/json-response-format)
     :response-format (ajax.core/json-response-format {:keywords? true})}))

(defn swap-state! [event]
  (let [value (-> event .-target .-value)
        game (if (empty? (:replay @state))
               demo-game
               (json->state (nth (:replay @state) (int value)) (:cards @state)))]
    (swap! state assoc-in [:value] value)
    (swap! state assoc-in [:game] game)))

(defn slider [param value max]
  [:input {:type "range"
           :value value
           :min 0
           :max max
           :style {:width "100%"}
           :on-change swap-state!}])

(defn file-loader []
  [:div#loader>input#files
    {:type "file"
     :name "files[]"
     :onChange read-file-input!}])

(defn swap-players-handler [e]
  (swap! state assoc-in [:players-swapped?] (not (:players-swapped? @state))))

(defn view-replay []
  (if (not (:cards-loaded @state)) (get-cards))
  [:div
    [slider :position (:value @state) (dec (count (:replay @state)))]
    [file-loader]
    [:div {:on-click swap-players-handler} "Swap"]
    [html/game @state]])

(defn home-page []
  [:div
   [:h2 "Welcome to replayviewer"]
   [:div [:a {:href "/replay"} "go to replay page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/replay" []
  (session/put! :current-page #'view-replay))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
