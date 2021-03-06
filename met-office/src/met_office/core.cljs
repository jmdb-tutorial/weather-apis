(ns ^:figwheel-always met-office.core
    (:require[om.core :as om :include-macros true]
             [om.dom :as dom :include-macros true]
             [goog.string :as gstring]
             [goog.string.format]
             [met-office.validation :as v]))

(enable-console-print!)

(println "3 +4 " (v/foo 3 4))

(println "Met Office API demonstration")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:api-urls [{:key :text-forecast-sites :link "http://datapoint.metoffice.gov.uk/public/data/txt/wxfcs/regionalforecast/json/sitelist?key=%s"}
                                     {:key :text-forecast-sw :link "http://datapoint.metoffice.gov.uk/public/data/txt/wxfcs/regionalforecast/json/513?key=%s"}
                                     {:key :observations-sites :link "http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/json/sitelist?key=%s"}
                                     {:key :observations-all :link "http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/json/sitelist?key=%s"}
                                     {:key :observations-camborne :link "http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/json/3808?res=hourly&key=%s"}]
                          }))

(defn format-link [link api-key]
  (gstring/format link api-key))

(defn api-link-view [api-url owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [href (format-link (:link api-url) (:api-key state))]
        (dom/li nil               
                (dom/span #js {:className "key"} (name (:key api-url)))
                (dom/a #js {:href href :target (name (:key api-url))} href))))))


(defn handle-change [e owner {:keys [api-key]}]
  (let [value (.. e -target -value)]
    (om/set-state! owner :api-key value)))

(defn api-links-view [data owner]
  (reify
    om/IInitState
    (init-state [_] {:api-key "<api key here>"})
    om/IRenderState
    (render-state [this state]      
      (dom/div nil
               (dom/div nil
                        (dom/label nil "API-KEY: ")
                        (dom/input #js {:size 60 :type "text" :ref "api-key" :value (:api-key state)
                                        :onChange #(handle-change % owner state)}))
               (dom/div nil
                        (dom/h2 nil "Available links")
                        (apply dom/ul nil
                               (om/build-all api-link-view (:api-urls data)
                                             {:init-state state
                                              :state state}))
                        )))))

(om/root api-links-view app-state
  {:target (. js/document (getElementById "api-links"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

