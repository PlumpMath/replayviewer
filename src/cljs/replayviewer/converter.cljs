(ns replayviewer.converter)

(defn in-play [e] (= (:ZONE (:Tags e)) 1))
(defn in-deck [e] (= (:ZONE (:Tags e)) 2))
(defn in-hand [e] (= (:ZONE (:Tags e)) 3))
(defn in-secret-land [e] (= (:ZONE (:Tags e)) 7))
(defn is-game [e] (= (:CARDTYPE (:Tags e)) 1))
(defn is-player [e] (= (:CARDTYPE (:Tags e)) 2))
(defn is-hero [e] (= (:CARDTYPE (:Tags e)) 3))
(defn is-minion [e] (= (:CARDTYPE (:Tags e)) 4))
(defn is-spell [e] (= (:CARDTYPE (:Tags e)) 5))
(defn is-effect [e] (= (:CARDTYPE (:Tags e)) 6))
(defn is-weapon [e] (= (:CARDTYPE (:Tags e)) 7))
(defn is-power [e] (= (:CARDTYPE (:Tags e)) 10))
(defn is-secret [e] (:SECRET (:Tags e)))
(defn owned-by-player [n] (fn [e] (= (:CONTROLLER (:Tags e)) n)))
(defn minion-from-data [data entities minion]
  {:name (:name (data (:CardId minion)))
   :health (:HEALTH (:Tags minion))
   :damage (int (:DAMAGE (:Tags minion)))
   :taunt (boolean (:TAUNT (:Tags minion)))
   :stealth (boolean (:STEALTH (:Tags minion)))
   :attack (:ATK (:Tags minion)) ;; plus any effects
   :immune false
   :frozen false
   :spellpower (:SPELLPOWER (:Tags minion))
   :charge (boolean (:CHARGE (:Tags minion)))
   :position (:ZONE_POSITION (:Tags minion))})
(defn secret-from-data [data secret]
  {:name (:name (data (:CardId secret)))})
(defn weapon-from-data [data weapon]
  (let [max-durability (:DURABILITY (:Tags weapon))
        uses (int (:DAMAGE (:Tags weapon)))
        durability (if max-durability (- max-durability uses) nil)]
    {:name (:name (data (:CardId weapon)))
     :durability durability
     :attack (:ATK (:Tags weapon))}))
(defn hero-from-data [data hero]
  (let [max-health (:HEALTH (:Tags hero))
        damage (int (:DAMAGE (:Tags hero)))
        health (- max-health damage)]
    {:name (:name (data (:CardId hero)))
     :health health
     :armor (:ARMOR (:Tags hero))
     :immune false
     :frozen false}))
(defn power-from-data [data power]
  {:name (:name (data (:CardId power)))
   :cost (:COST (:Tags power))
   :used (:EXHAUSTED (:Tags power))})
(defn card-in-hand-from-data [data card]
  (let [name (:name (data (:CardId card)))]
    {:name (or name "Unknown")
     :cost (or (:COST (:Tags card)) (:cost (data (:CardId card))))
     :data (data (:CardId card))}))

(defn minions [n entities]
  (filter (every-pred in-play is-minion (owned-by-player n)) entities))

(defn effects [n entities]
  (filter (every-pred in-play is-effect (owned-by-player n)) entities))

(defn board [n data]
  (->> (concat (minions n (:entities data)) (effects n (:entities data)))
    (map (partial minion-from-data (:cards data) (:entities data)))
    (map-indexed (fn [idx elem] (assoc elem :position idx)))))

(defn secrets [n data]
  (->> (:entities data)
    (filter in-secret-land)
    (filter is-secret)
    (filter (owned-by-player n))
    (map (partial secret-from-data (:cards data)))
    (map-indexed (fn [idx elem] (assoc elem :position idx)))))

(defn weapon [n data]
  (->> (:entities data)
    (filter in-play)
    (filter is-weapon)
    (filter (owned-by-player n))
    first
    (weapon-from-data (:cards data))))

(defn hero [n data]
  (->> (:entities data)
    (filter in-play)
    (filter is-hero)
    (filter (owned-by-player n))
    first
    (hero-from-data (:cards data))))

(defn power [n data]
  (->> (:entities data)
    (filter in-play)
    (filter is-power)
    (filter (owned-by-player n))
    first
    (power-from-data (:cards data))))

(defn hand [n data]
  (->> (:entities data)
    (filter in-hand)
    (filter (owned-by-player n))
    (map (partial card-in-hand-from-data (:cards data)))
    (map-indexed (fn [idx elem] (assoc elem :position idx)))))

(defn deck [n data]
  (filter (every-pred in-deck (owned-by-player n)) (:entities data)))