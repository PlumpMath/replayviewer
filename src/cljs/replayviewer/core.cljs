(ns replayviewer.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [ajax.core]))

;; -------------------------
;; Views

;; A player is a map and is either on top or bottom

;; {
;;   "Data":
;;   [
;;     {"Info":null,"Tags":{"ZONE":1,"ENTITY_ID":1,"CARDTYPE":1,"STATE":2,"TURN":15,"10":85,"NEXT_STEP":0,"STEP":10,"PROPOSED_ATTACKER":0,"PROPOSED_DEFENDER":0,"NUM_MINIONS_KILLED_THIS_TURN":0},"Name":"GameEntity","Id":1,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"HERO_ENTITY":64,"MAXHANDSIZE":10,"STARTHANDSIZE":4,"PLAYER_ID":1,"TEAM_ID":1,"ZONE":1,"CONTROLLER":1,"ENTITY_ID":2,"MAXRESOURCES":10,"CARDTYPE":2,"PLAYSTATE":1,"CURRENT_PLAYER":1,"FIRST_PLAYER":1,"NUM_CARDS_DRAWN_THIS_TURN":1,"NUM_TURNS_LEFT":1,"TIMEOUT":75,"MULLIGAN_STATE":4,"RESOURCES":8,"RESOURCES_USED":8,"NUM_RESOURCES_SPENT_THIS_GAME":31,"NUM_CARDS_PLAYED_THIS_TURN":1,"NUM_MINIONS_PLAYED_THIS_TURN":1,"LAST_CARD_PLAYED":28,"COMBO_ACTIVE":0,"NUM_OPTIONS_PLAYED_THIS_TURN":0,"HEROPOWER_ACTIVATIONS_THIS_TURN":0,"NUM_TIMES_HERO_POWER_USED_THIS_GAME":2,"NUM_FRIENDLY_MINIONS_THAT_ATTACKED_THIS_TURN":0,"NUM_MINIONS_PLAYER_KILLED_THIS_TURN":0,"NUM_FRIENDLY_MINIONS_THAT_DIED_THIS_TURN":0,"NUM_FRIENDLY_MINIONS_THAT_DIED_THIS_GAME":5,"RECALL_OWED":0,"OVERLOAD_THIS_GAME":5,"OVERLOAD_LOCKED":3,"430":0},"Name":"AbsoluteAres","Id":2,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":true,"HasCardId":false},
;;     {"Info":null,"Tags":{"HERO_ENTITY":66,"MAXHANDSIZE":10,"STARTHANDSIZE":4,"PLAYER_ID":2,"TEAM_ID":2,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":3,"MAXRESOURCES":10,"CARDTYPE":2,"PLAYSTATE":1,"NUM_CARDS_DRAWN_THIS_TURN":1,"NUM_TURNS_LEFT":1,"TIMEOUT":75,"MULLIGAN_STATE":4,"CURRENT_PLAYER":0,"RESOURCES":7,"NUM_CARDS_PLAYED_THIS_TURN":2,"430":2,"LAST_CARD_PLAYED":34,"TEMP_RESOURCES":0,"COMBO_ACTIVE":1,"NUM_OPTIONS_PLAYED_THIS_TURN":9,"RESOURCES_USED":6,"NUM_RESOURCES_SPENT_THIS_GAME":28,"NUM_MINIONS_PLAYED_THIS_TURN":0,"NUM_MINIONS_PLAYER_KILLED_THIS_TURN":0,"NUM_FRIENDLY_MINIONS_THAT_DIED_THIS_TURN":0,"NUM_FRIENDLY_MINIONS_THAT_DIED_THIS_GAME":6,"WEAPON":0,"NUM_FRIENDLY_MINIONS_THAT_ATTACKED_THIS_TURN":0,"HEROPOWER_ACTIVATIONS_THIS_TURN":0,"NUM_TIMES_HERO_POWER_USED_THIS_GAME":1},"Name":"Deetle","Id":3,"CardId":null,"IsPlayer":true,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":4},"Name":null,"Id":4,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":5},"Name":null,"Id":5,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":6,"ZONE_POSITION":0,"HEALTH":7,"ATK":7,"COST":4,"CARDTYPE":4,"RARITY":0,"OVERLOAD":1,"RECALL_OWED":0,"TAG_LAST_KNOWN_COST_IN_HAND":4,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":3,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":70,"DAMAGE":0},"Name":null,"Id":6,"CardId":"OG_024","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":7},"Name":null,"Id":7,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":8,"ZONE_POSITION":0,"COST":1,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":1,"JUST_PLAYED":1},"Name":null,"Id":8,"CardId":"OG_027","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":9},"Name":null,"Id":9,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":1,"CONTROLLER":1,"ENTITY_ID":10,"ZONE_POSITION":1,"HEALTH":4,"ATK":4,"COST":5,"SPELLPOWER":1,"FACTION":0,"CARDTYPE":4,"RARITY":0,"BATTLECRY":1,"TAG_LAST_KNOWN_COST_IN_HAND":5},"Name":null,"Id":10,"CardId":"EX1_284","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":11},"Name":null,"Id":11,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":12,"ZONE_POSITION":0},"Name":null,"Id":12,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":6,"CONTROLLER":1,"ENTITY_ID":13,"ZONE_POSITION":0,"HEALTH":2,"ATK":3,"COST":3,"CARDTYPE":4,"RARITY":0,"BATTLECRY":1,"TAG_LAST_KNOWN_COST_IN_HAND":3,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":2,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":1,"LINKEDCARD":85},"Name":null,"Id":13,"CardId":"AT_046","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":14},"Name":null,"Id":14,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":3,"CONTROLLER":1,"ENTITY_ID":15,"ZONE_POSITION":2},"Name":null,"Id":15,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":16},"Name":null,"Id":16,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":17,"ZONE_POSITION":0,"TRIGGER_VISUAL":1,"HEALTH":3,"ATK":1,"COST":1,"CARDTYPE":4,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":1,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":4,"ATTACKING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":44,"DAMAGE":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":17,"CardId":"LOE_018","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":6,"CONTROLLER":1,"ENTITY_ID":18,"ZONE_POSITION":0,"HEALTH":5,"ATK":5,"COST":6,"TAUNT":1,"CARDTYPE":4,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":2,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":1,"LINKEDCARD":84},"Name":null,"Id":18,"CardId":"OG_028","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":19,"ZONE_POSITION":0},"Name":null,"Id":19,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":20},"Name":null,"Id":20,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":21},"Name":null,"Id":21,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":22},"Name":null,"Id":22,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":23,"ZONE_POSITION":0,"COST":2,"CARDTYPE":5,"RARITY":0,"OVERLOAD":1,"RECALL_OWED":0,"TAG_LAST_KNOWN_COST_IN_HAND":2,"JUST_PLAYED":1,"CARD_TARGET":73},"Name":null,"Id":23,"CardId":"OG_206","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":24,"ZONE_POSITION":0},"Name":null,"Id":24,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":25},"Name":null,"Id":25,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":26},"Name":null,"Id":26,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":27},"Name":null,"Id":27,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":1,"CONTROLLER":1,"ENTITY_ID":28,"ZONE_POSITION":5,"HEALTH":7,"ATK":7,"COST":4,"CARDTYPE":4,"RARITY":0,"OVERLOAD":1,"RECALL_OWED":0,"TAG_LAST_KNOWN_COST_IN_HAND":4,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":2,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":79,"DAMAGE":1},"Name":null,"Id":28,"CardId":"OG_024","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":29,"ZONE_POSITION":0,"COST":1,"FACTION":0,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":1,"JUST_PLAYED":1,"CARD_TARGET":64},"Name":null,"Id":29,"CardId":"CS2_045","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":30},"Name":null,"Id":30,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":1,"ENTITY_ID":31},"Name":null,"Id":31,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":3,"CONTROLLER":1,"ENTITY_ID":32,"ZONE_POSITION":1},"Name":null,"Id":32,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":33,"ZONE_POSITION":0,"TRIGGER_VISUAL":1,"HEALTH":3,"COST":3,"FACTION":0,"CARDTYPE":4,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":1,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":66,"DAMAGE":0},"Name":null,"Id":33,"CardId":"EX1_575","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":34,"COST":3,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":0,"JUST_PLAYED":1},"Name":null,"Id":34,"CardId":"NEW1_031","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":35},"Name":null,"Id":35,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":36,"COST":6,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":6,"ZONE_POSITION":0,"JUST_PLAYED":1},"Name":null,"Id":36,"CardId":"AT_062","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":37},"Name":null,"Id":37,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":38},"Name":null,"Id":38,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":39},"Name":null,"Id":39,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":40},"Name":null,"Id":40,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":41},"Name":null,"Id":41,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":3,"CONTROLLER":2,"ENTITY_ID":42,"HEALTH":3,"ATK":3,"COST":4,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"TAG_LAST_KNOWN_COST_IN_HAND":4,"ZONE_POSITION":1},"Name":null,"Id":42,"CardId":"OG_216","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":43,"COST":3,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":0,"JUST_PLAYED":1},"Name":null,"Id":43,"CardId":"NEW1_031","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":44,"COST":2,"CLASS":3,"FACTION":0,"CARDTYPE":5,"RARITY":0,"SECRET":1,"TAG_LAST_KNOWN_COST_IN_HAND":2,"ZONE_POSITION":0,"JUST_PLAYED":1,"EXHAUSTED":0},"Name":null,"Id":44,"CardId":"EX1_610","IsPlayer":false,"IsSecret":true,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":45},"Name":null,"Id":45,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":46,"HEALTH":1,"ATK":2,"COST":2,"FACTION":0,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"TAG_LAST_KNOWN_COST_IN_HAND":2,"ZONE_POSITION":0,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":1,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":17,"DAMAGE":0},"Name":null,"Id":46,"CardId":"EX1_096","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":47},"Name":null,"Id":47,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":48},"Name":null,"Id":48,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":49},"Name":null,"Id":49,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":50},"Name":null,"Id":50,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":51},"Name":null,"Id":51,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":52,"HEALTH":3,"ATK":1,"COST":3,"CARDTYPE":4,"RARITY":0,"BATTLECRY":1,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":0,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":2,"ATTACKING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":6,"DAMAGE":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":52,"CardId":"KAR_030a","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":53},"Name":null,"Id":53,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":54},"Name":null,"Id":54,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":55},"Name":null,"Id":55,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":56,"TRIGGER_VISUAL":1,"HEALTH":2,"ATK":1,"COST":1,"FACTION":0,"CARDTYPE":4,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":1,"ZONE_POSITION":0,"EXHAUSTED":0,"JUST_PLAYED":0,"NUM_TURNS_IN_PLAY":1,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":64,"DAMAGE":0},"Name":null,"Id":56,"CardId":"EX1_080","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":3,"CONTROLLER":2,"ENTITY_ID":57,"TRIGGER_VISUAL":1,"HEALTH":1,"ATK":1,"COST":3,"ELITE":1,"STEALTH":1,"CARDTYPE":4,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":3},"Name":null,"Id":57,"CardId":"KAR_044","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":58},"Name":null,"Id":58,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":59,"COST":3,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":0,"JUST_PLAYED":1},"Name":null,"Id":59,"CardId":"EX1_538","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":60},"Name":null,"Id":60,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":3,"CONTROLLER":2,"ENTITY_ID":61,"COST":8,"CARDTYPE":5,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":8,"ZONE_POSITION":2},"Name":null,"Id":61,"CardId":"OG_211","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":2,"CONTROLLER":2,"ENTITY_ID":62},"Name":null,"Id":62,"CardId":null,"IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":false},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":63,"TRIGGER_VISUAL":1,"ATK":3,"COST":3,"DURABILITY":2,"CARDTYPE":7,"RARITY":0,"TAG_LAST_KNOWN_COST_IN_HAND":3,"ZONE_POSITION":0,"JUST_PLAYED":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":3,"DAMAGE":0,"EXHAUSTED":0,"NUM_TURNS_IN_PLAY":4},"Name":null,"Id":63,"CardId":"EX1_536","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":30,"ZONE":1,"CONTROLLER":1,"ENTITY_ID":64,"FACTION":0,"CARDTYPE":3,"RARITY":0,"SHOWN_HERO_POWER":687,"NUM_TURNS_IN_PLAY":15,"PREDAMAGE":0,"LAST_AFFECTED_BY":89,"DAMAGE":11,"OneTurnEffect":0,"ATK":0,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":0,"DEFENDING":0},"Name":null,"Id":64,"CardId":"HERO_02","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"COST":2,"ZONE":1,"CONTROLLER":1,"ENTITY_ID":65,"FACTION":0,"CARDTYPE":10,"RARITY":0,"CREATOR":64,"TAG_LAST_KNOWN_COST_IN_HAND":2,"NUM_TURNS_IN_PLAY":15,"EXHAUSTED":0},"Name":null,"Id":65,"CardId":"CS2_049","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":true,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":30,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":66,"FACTION":0,"CARDTYPE":3,"RARITY":0,"SHOWN_HERO_POWER":229,"NUM_TURNS_IN_PLAY":15,"ATK":0,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":0,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":72,"DAMAGE":24},"Name":null,"Id":66,"CardId":"HERO_05","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"COST":2,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":67,"FACTION":0,"CARDTYPE":10,"RARITY":0,"CREATOR":66,"TAG_LAST_KNOWN_COST_IN_HAND":2,"NUM_TURNS_IN_PLAY":15,"EXHAUSTED":0},"Name":null,"Id":67,"CardId":"DS1h_292","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":true,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":68,"CARDTYPE":5,"ZONE_POSITION":0,"CREATOR":1,"JUST_PLAYED":1},"Name":null,"Id":68,"CardId":"GAME_005","IsPlayer":false,"IsSecret":false,"IsSpell":true,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":1,"ATK":1,"COST":1,"ZONE":4,"CONTROLLER":1,"ENTITY_ID":69,"FACTION":0,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":0,"CREATOR":65,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":2,"PREDAMAGE":0,"LAST_AFFECTED_BY":44,"DAMAGE":0},"Name":null,"Id":69,"CardId":"CS2_050","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":3,"ATK":1,"COST":3,"ZONE":4,"CONTROLLER":2,"ENTITY_ID":70,"CARDTYPE":4,"ZONE_POSITION":0,"CREATOR":52,"TAG_LAST_KNOWN_COST_IN_HAND":3,"NUM_TURNS_IN_PLAY":2,"ATTACKING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":6,"DAMAGE":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":70,"CardId":"KAR_030","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":1,"ENTITY_ID":71,"ATTACHED":0,"CARDTYPE":6,"CREATOR":29,"OneTurnEffect":1,"LAST_AFFECTED_BY":71},"Name":null,"Id":71,"CardId":"CS2_045e","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":1,"ATK":1,"COST":1,"ZONE":6,"CONTROLLER":1,"ENTITY_ID":72,"FACTION":0,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":0,"CREATOR":65,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":4,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":1,"LINKEDCARD":83},"Name":null,"Id":72,"CardId":"CS2_050","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":4,"ATK":2,"COST":3,"ZONE":4,"CONTROLLER":2,"ENTITY_ID":73,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":0,"CREATOR":43,"TAG_LAST_KNOWN_COST_IN_HAND":3,"NUM_TURNS_IN_PLAY":3,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":23,"DAMAGE":0},"Name":null,"Id":73,"CardId":"NEW1_033","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":74,"CARDTYPE":6,"CREATOR":73,"ATTACHED":0,"323":0,"324":0},"Name":null,"Id":74,"CardId":"NEW1_033o","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":75,"CARDTYPE":6,"CREATOR":73,"ATTACHED":0,"323":0,"324":0},"Name":null,"Id":75,"CardId":"NEW1_033o","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":2,"COST":1,"ZONE":4,"CONTROLLER":1,"ENTITY_ID":76,"TAUNT":1,"FACTION":0,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":0,"CREATOR":13,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":1,"DEFENDING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":73,"DAMAGE":0},"Name":null,"Id":76,"CardId":"CS2_051","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":1,"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":77,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"ZONE_POSITION":1,"CREATOR":36,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":3,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":77,"CardId":"FP1_011","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":1,"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":78,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"ZONE_POSITION":2,"CREATOR":36,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":3,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":78,"CardId":"FP1_011","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":0,"HEALTH":1,"ATK":1,"COST":1,"ZONE":4,"CONTROLLER":2,"ENTITY_ID":79,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"ZONE_POSITION":0,"CREATOR":36,"TAG_LAST_KNOWN_COST_IN_HAND":1,"NUM_TURNS_IN_PLAY":2,"ATTACKING":0,"PREDAMAGE":0,"LAST_AFFECTED_BY":28,"DAMAGE":0,"NUM_ATTACKS_THIS_TURN":0},"Name":null,"Id":79,"CardId":"FP1_011","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":80,"CARDTYPE":6,"CREATOR":73,"ATTACHED":0,"323":0,"324":0,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":80,"CardId":"NEW1_033o","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":81,"CARDTYPE":6,"CREATOR":73,"ATTACHED":0,"323":0,"324":0,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":81,"CardId":"NEW1_033o","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":4,"CONTROLLER":2,"ENTITY_ID":82,"CARDTYPE":6,"CREATOR":73,"ATTACHED":0,"323":0,"324":0,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":82,"CardId":"NEW1_033o","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":1,"CONTROLLER":1,"ENTITY_ID":83,"HEALTH":4,"ATK":4,"COST":2,"ELITE":1,"CARDTYPE":4,"RARITY":0,"BATTLECRY":1,"LINKEDCARD":72,"CREATOR":8,"341":0,"REVEALED":1,"TAG_LAST_KNOWN_COST_IN_HAND":2,"ZONE_POSITION":3,"EXHAUSTED":0,"NUM_TURNS_IN_PLAY":2},"Name":null,"Id":83,"CardId":"NEW1_029","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":1,"CONTROLLER":1,"ENTITY_ID":84,"HEALTH":4,"ATK":8,"COST":7,"ELITE":1,"CARDTYPE":4,"RARITY":0,"BATTLECRY":1,"LINKEDCARD":18,"CREATOR":8,"341":0,"REVEALED":1,"TAG_LAST_KNOWN_COST_IN_HAND":7,"ZONE_POSITION":4,"EXHAUSTED":0,"NUM_TURNS_IN_PLAY":2},"Name":null,"Id":84,"CardId":"BRM_029","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"ZONE":1,"CONTROLLER":1,"ENTITY_ID":85,"HEALTH":2,"ATK":4,"COST":4,"CARDTYPE":4,"RARITY":0,"DEATH_RATTLE":1,"LINKEDCARD":13,"CREATOR":8,"341":0,"REVEALED":1,"TAG_LAST_KNOWN_COST_IN_HAND":4,"ZONE_POSITION":2,"EXHAUSTED":0,"NUM_TURNS_IN_PLAY":2},"Name":null,"Id":85,"CardId":"OG_323","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":86,"CHARGE":1,"CARDTYPE":4,"ZONE_POSITION":3,"CREATOR":59,"TAG_LAST_KNOWN_COST_IN_HAND":1,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":1,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":86,"CardId":"EX1_538t","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":87,"CHARGE":1,"CARDTYPE":4,"ZONE_POSITION":4,"CREATOR":59,"TAG_LAST_KNOWN_COST_IN_HAND":1,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":1,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":87,"CardId":"EX1_538t","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":88,"CHARGE":1,"CARDTYPE":4,"ZONE_POSITION":5,"CREATOR":59,"TAG_LAST_KNOWN_COST_IN_HAND":1,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":1,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":88,"CardId":"EX1_538t","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"HEALTH":1,"ATK":1,"COST":1,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":89,"CHARGE":1,"CARDTYPE":4,"ZONE_POSITION":6,"CREATOR":59,"TAG_LAST_KNOWN_COST_IN_HAND":1,"ATTACKING":0,"NUM_ATTACKS_THIS_TURN":0,"EXHAUSTED":1,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":89,"CardId":"EX1_538t","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"TRIGGER_VISUAL":1,"HEALTH":8,"ATK":8,"COST":8,"ZONE":3,"CONTROLLER":2,"ENTITY_ID":90,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":4,"CREATOR":79,"TAG_LAST_KNOWN_COST_IN_HAND":8,"DISPLAYED_CREATOR":79},"Name":null,"Id":90,"CardId":"OG_308","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true},
;;     {"Info":null,"Tags":{"EXHAUSTED":1,"HEALTH":4,"ATK":4,"COST":3,"ZONE":1,"CONTROLLER":2,"ENTITY_ID":91,"TAUNT":1,"CARDTYPE":4,"RARITY":0,"ZONE_POSITION":7,"CREATOR":34,"TAG_LAST_KNOWN_COST_IN_HAND":3,"NUM_TURNS_IN_PLAY":1},"Name":null,"Id":91,"CardId":"NEW1_032","IsPlayer":false,"IsSecret":false,"IsSpell":false,"IsHeroPower":false,"IsCurrentPlayer":false,"HasCardId":true}
;;   ],
;;   "Id":10,
;;   "Player":1,
;;   "Type":0,
;;   "Turn":15
;; }

(def ^:export cards (atom [{}]))
(defn ^:export cardscljs [] (clj->js (into [] (vals @cards))))

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
(defn minion-from-data [entities minion]
  {:name (:name (@cards (:CardId minion)))
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
(defn secret-from-data [secret]
  {:name (:name (@cards (:CardId secret)))})
(defn weapon-from-data [weapon]
  (let [max-durability (:DURABILITY (:Tags weapon))
        uses (int (:DAMAGE (:Tags weapon)))
        durability (if max-durability (- max-durability uses) nil)]
    {:name (:name (@cards (:CardId weapon)))
     :durability durability
     :attack (:ATK (:Tags weapon))}))
(defn hero-from-data [hero]
  (let [max-health (:HEALTH (:Tags hero))
        damage (int (:DAMAGE (:Tags hero)))
        health (- max-health damage)]
    {:name (:name (@cards (:CardId hero)))
     :health health
     :armor (:ARMOR (:Tags hero))
     :immune false
     :frozen false}))
(defn power-from-data [power]
  {:name (:name (@cards (:CardId power)))
   :cost (:COST (:Tags power))
   :used (:EXHAUSTED (:Tags power))})
(defn card-in-hand-from-data [card]
  {:name (:name (@cards (:CardId card)))
   :cost (:COST (:Tags card))})

(defn player-from-data [n entities]
  (let [minions (filter (every-pred in-play is-minion (owned-by-player n)) entities)
        effects (filter (every-pred in-play is-effect (owned-by-player n)) entities)
        board (concat minions effects)
        secrets (filter (every-pred in-secret-land is-secret (owned-by-player n)) entities)
        weapon (filter (every-pred in-play is-weapon (owned-by-player n)) entities)
        hero (filter (every-pred in-play is-hero (owned-by-player n)) entities)
        power (filter (every-pred in-play is-power (owned-by-player n)) entities)
        hand (filter (every-pred in-hand (owned-by-player n)) entities)
        deck (filter (every-pred in-deck (owned-by-player n)) entities)]
    {:board (map (fn [e] (minion-from-data entities e)) board)
     :secrets (map secret-from-data secrets)
     :weapon (weapon-from-data (first weapon))
     :hero (hero-from-data (first hero))
     :power (power-from-data (first power))
     :hand (map card-in-hand-from-data hand)
     :deck deck
     :top (if (= 1 n) true false)}))

(defn json->state [json]
  (let [entities (:Data json)
        turn (:Turn json)]
    {:players
      [(player-from-data 1 entities)
       (player-from-data 2 entities)]}))

(def demo-board
  [{:health 3 :name "Fen Creeper" :attack 6}
   {:name "Web Spinner" :health 1 :attack 1}
   {:name "Web Spinner" :health 1 :attack 1}
   {:name "Web Spinner" :health 1 :attack 1}
   #_{:name "Leokk" :health 4 :attack 2}])

(def demo-secrets
  [{:name "Dart Trap"}
   {:name "Explosive Trap"}])

(def demo-weapon
  {:durability 2 :attack 3 :name "Eaglehorn Bow"})

(def demo-hero
  {:name "Rexxar" :health 26 :armor 0})

(def demo-power
  {:name "Hit Face for 2" :cost 2 :used false})

(def demo-hand
  [{:name "Animal Bite" :cost 3}
   {:name "Wisp" :cost 0}])

(def demo-deck
  [{:name "Alexstraza" :cost 9}])

(def demo-player
  {:board demo-board
   :secrets demo-secrets
   :weapon demo-weapon
   :hero demo-hero
   :power demo-power
   :hand demo-hand
   :deck demo-deck
   :name "Deetle"
   :top false})

(def demo-player-two
  {:board demo-board
   :secrets demo-secrets
   :weapon demo-weapon
   :hero demo-hero
   :power demo-power
   :hand demo-hand
   :deck demo-deck
   :name "Other"
   :top true})

;; A replay is an ordered sequence of game states
(def replay (atom [{}]))

(def current-bottom-player demo-player)
(def current-top-player demo-player-two)

;; A game state is a map
(def current-state (atom
  {:players [current-top-player current-bottom-player]
   :turn 0
   }))

(defn load-replay [event]
  (let [file (js/JSON.parse (-> event .-target .-result))]
    (reset! replay (js->clj file :keywordize-keys true))))

(defn file-input-change-listener [files]
  (let [file (.item files 0)
        reader (js/FileReader.)
        onload load-replay]
    (aset reader "onload" onload)
    (.readAsText reader file)))

(defn get-stuff []
  (ajax.core/ajax-request
    {:uri "/replay.json"
     :method :get
     :handler (fn [[ok response]] (reset! replay response) (js/console.log "loaded replay"))
     :format (ajax.core/json-response-format)
     :response-format (ajax.core/json-response-format {:keywords? true})})
  (ajax.core/ajax-request
    {:uri "/cards.json"
     :method :get
     :handler (fn [[ok response]]
       (reset! cards
         (->> response
           vals
           (mapcat (fn [card-list] (map (fn [card] {(:cardId card) card}) card-list)))
           (reduce merge {})))
      (js/console.log "loaded cards"))
     :format (ajax.core/json-response-format)
     :response-format (ajax.core/json-response-format {:keywords? true})}))

(def replay-file (get-stuff))

(defn players [replay]
  )

(defn display-entity [entity]
  (let [entity (merge (dissoc entity :Tags) (entity :Tags))]
  [:table
  [:tbody
    [:tr
      (for [k (keys entity)]
        ^{:key k} [:td k])]
    [:tr
      (for [k (keys entity)]
        ^{:key k} [:td (str(k entity))])]]]))

(defn display-board [board]
  [:div.board
    [:div.minions
    (doall (for [[idx minion] (map-indexed vector board)]
          ^{:key idx}
          [:div.minion
            [:span (:name minion)]
            [:span.attack (:attack minion)]
            [:span.health (- (:health minion) (:damage minion))]]))]])

(defn display-secrets [secrets]
  [:div.secrets
    (doall (for [[idx secret] (map-indexed vector secrets)]
          ^{:key idx} [:span (:name secret)]))])

(defn display-weapon [weapon]
  [:div.weapon (:name weapon) (:attack weapon) (:durability weapon)])

(defn display-hero [hero]
  [:div.hero
    [:span (:name hero)]
    [:span (:health hero)
    [:span (:armor hero)]]])

(defn display-power [power]
  [:div.power
    [:span (:name power)]
    [:span (:cost power)]
    [:span (:used power)]])

(defn display-deck [deck]
  [:div.deck
    [:span (count deck)]])

(defn display-hand [hand]
  [:div.hand
    (doall (for [[idx card] (map-indexed vector hand)]
          ^{:key idx}
          [:div.card-in-hand
            [:span (:name card)]
            [:span (:cost card)]]))])

(defn display-player-bottom [player]
    [:div.player-bottom
      (display-board (:board player))
      (display-secrets (:secrets player))
      (display-weapon (:weapon player))
      (display-hero (:hero player))
      (display-power (:power player))
      (display-deck (:deck player))
      (display-hand (:hand player))])

(defn display-player-top [player]
    [:div.player-top
      (display-board (:board player))
      (display-secrets (:secrets player))
      (display-weapon (:weapon player))
      (display-hero (:hero player))
      (display-power (:power player))
      (display-deck (:deck player))
      (display-hand (:hand player))])

(defn display-player [player]
  (if (:top player)
    (display-player-top player)
    (display-player-bottom player)))

(defn in-play? [entity]
  (= 1 (:ZONE (:Tags entity))))

(defn is-minion? [entity]
  ;; not a weapon
  ;; not a hero
  ;; not a secret
  ;; not a power
  ;; not a player
  ;; has a card id
  (:HasCardId entity))

(defn json-to-minion-map [minion]
  {:name (:CardId minion)
   :health (:HEALTH (:Tags minion))
   :attack (:ATTACK (:Tags minion))})

(defn minions-in-play [entities]
  (->> entities
    (filter (fn [e] (and (in-play? e) (is-minion? e))))
    (map json-to-minion-map)))

(defn make-player [board]
  {:board board
   :secrets demo-secrets
   :weapon demo-weapon
   :hero demo-hero
   :power demo-power
   :hand demo-hand
   :deck demo-deck
   :name "Deetle"
   :top false}
  )

(defn json-to-state [json]
  (let [entities (:Data json)
        board (minions-in-play entities)
        top-player current-top-player
        bottom-player (make-player board)]
    {:players [top-player (atom bottom-player)] :turn 0}))

(defn swap-nth-state [n]
  (reset! current-state (json->state (nth @replay (int n)))))

(def current-state-value (atom 0))

(defn slider [param value max]
  [:input {:type "range" :value value :min 0 :max (dec max)
           :style {:width "100%"}
           :on-change (fn [e] (reset! current-state-value (-> e .-target .-value)) (swap-nth-state (-> e .-target .-value)))}])

(defn display-game-state [state]
  [:div
  (display-player (first (:players state)))
  (display-player (second (:players state)))])

(defn view-replay []
  (let [a replay-file]
  [:div
    [slider :position @current-state-value (count @replay)]

    [:div#loader
      [:input#files {:type "file"
                     :name "files[]"
                     :onChange (fn [event] (file-input-change-listener (-> event .-target .-files)))}]]
    #_(-> (@replay 0) :Data (nth 12) display-entity)
    (display-game-state @current-state)
    [:div#cards (str #_(vals @cards))]]))

(defn home-page []
  [:div [:h2 "Welcome to replayviewer"]
   [:div [:a {:href "/replay"} "go to replay page"]]
   [:div [:a {:href "/about"} "go to ss page"]]])

(defn about-page []
  [:div [:h2 "About replayviewer"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/replay" []
  (session/put! :current-page #'view-replay))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

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
