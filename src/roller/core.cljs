(ns roller.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/roller/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom {:roller-y 285
         :jump-start-time 0
         :jump-happening false}))

(defn game-world []
  (let [{:keys [roller-y]} @app-state]
    [:div.content
     [:div.roller {:style {:top (str roller-y "px")}}]]))

(reagent/render-component [game-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn calculate-x [time-start]
  (let [interval 500 time-current (.now js/Date)]
    (if (= time-start time-current)
      0
      (do
        (/ (- time-current time-start) interval)))))

(defn calculate-jump-position [x]
  (* 100 (+ 2 (.sin js/Math x))))

(defn update-roller-y [jump-started]
  (calculate-jump-position (calculate-x jump-started)))

(defn update-state [{:keys [jump-started]}]
  {:roller-y (update-roller-y jump-started)
   :jump-started jump-started})

(defn update-game []
  (swap! app-state update-state))

(defn time-loop []
  (update-game)
  (.requestAnimationFrame js/window time-loop))

(aset js/document "onkeypress"
      (fn [e]
        (if (= 32 (aget e "charCode"))
          (do
            (println "enter pressed")
            (swap! app-state assoc :jump-started (.now js/Date))
            (.requestAnimationFrame js/window time-loop))
          (println "no"))))

