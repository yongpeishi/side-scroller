(ns roller.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/roller/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def ^:const jump-height 150)
(def ^:const jump-speed 60)
(def ^:const base-position 285)

(defonce app-state
  (atom {:roller-y base-position
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

(defn calculate-sin-y [x]
  (* jump-height (.sin js/Math (* x (aget js/Math "PI")))))

(defn calculate-jump-position [n]
  (let [y (calculate-sin-y (/ n jump-speed))]
    (- base-position y)))

(defn update-state [{:keys [jump-start-time jump-happening] :as state} n]
  (let [new-y (calculate-jump-position n)]
    (if (>= new-y base-position)
      {:roller-y base-position :jump-start-time 0 :jump-happening false}
      {:roller-y new-y :jump-start-time jump-start-time :jump-happening true})))

(defn update-game [n]
  (swap! app-state update-state n))

(defn time-loop [n]
  (update-game n)
  (if (< n jump-speed)
    (.requestAnimationFrame js/window (partial time-loop (inc n)))))

(aset js/document "onkeypress"
      (fn [e]
        (if (= 32 (aget e "charCode"))
          (do
            (println "enter pressed")
            (swap! app-state assoc :jump-start-time (.now js/Date) :jump-happening true)
            (.requestAnimationFrame js/window (partial time-loop 0)))
          (println "no"))))

