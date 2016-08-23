(ns replayviewer.prod
  (:require [replayviewer.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
