(ns re-frame-with-firebase.events
  (:require
   [re-frame.core :as re-frame]
   [re-frame-with-firebase.db :as db]
   [re-frame-with-firebase.firebase.db :refer [update-doc user-path]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-user
 (fn [db [event-name user]]
   (assoc db :user user :user-loading? false)))

(re-frame/reg-event-db
 ::set-food-items
 (fn [db [_ items]]
   (assoc db :food-items items)))

(re-frame/reg-event-db
 ::set-food-input
 (fn [db [_ value]]
   (assoc db :food-input value)))

(re-frame/reg-event-db
 ::add-food-item
 (fn [db _]
   (let [new-food-list (conj (:food-items db) (:food-input db))]
     (update-doc (user-path (:user db)) {:food-items new-food-list})
     (assoc db
            :food-input ""
            :food-items new-food-list))))
