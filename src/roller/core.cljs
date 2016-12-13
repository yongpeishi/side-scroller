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
         :jump-happening false}))

(defn game-world []
  (let [{:keys [roller-y]} @app-state]
    [:div.content
     [:div.roller {:style {:top (str roller-y "px")}}]]))

(reagent/render-component [game-world]
                          (. js/document (getElementById "app")))

(defn sine-y-value [n]
  (let [x (* (/ n jump-speed) (aget js/Math "PI"))]
    (* jump-height (.sin js/Math x))))

(defn calculate-jump-position [n]
  (- base-position (sine-y-value n)))

(defn update-state [_ n]
  (let [new-y (calculate-jump-position n)]
    (if (>= new-y base-position)
      {:roller-y base-position :jump-happening false}
      {:roller-y new-y :jump-happening true})))

(defn time-loop [n]
  (swap! app-state update-state n)
  (if (< n jump-speed)
    (.requestAnimationFrame js/window (partial time-loop (inc n)))))

(aset js/document "onkeypress"
      (fn [e]
        (if (= 32 (aget e "charCode"))
          (do
            (println "enter pressed")
            (if-not (get @app-state :jump-happening)
              (.requestAnimationFrame js/window (partial time-loop 0))))
          (println "no"))))

