(ns roller.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "This text is printed from src/roller/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom {:roller-y 312}))

(defn game-world []
  (let [{:keys [roller-y]} @app-state]
    [:div
     [:div.roller {:style {:top (str roller-y "px")} }]]))

(reagent/render-component [game-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn update-roller []
  (swap! app-state (fn [{:keys [roller-y]}]
                     {:roller-y
                      (if (<= roller-y 100)
                        100
                        (- roller-y 10))})))

(defn time-loop [timestamp]
  (update-roller)
  (.requestAnimationFrame js/window time-loop)
  )

(aset js/document "onkeypress"
      (fn [e]
        (if (= 32 (aget e "charCode"))
          (do
           (println "enter pressed")
           (.requestAnimationFrame js/window time-loop))
          (println "no"))))

