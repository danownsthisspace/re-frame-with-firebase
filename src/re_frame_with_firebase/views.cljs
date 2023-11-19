(ns re-frame-with-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [re-frame-with-firebase.firebase.init :refer [sign-out google-sign-in]]
   [re-frame-with-firebase.subs :as subs]
   [re-frame-with-firebase.events :as events]
   [re-frame-with-firebase.firebase.db :refer [fetch-doc user-path]]))

(defn food-list [user]
  (let [_ (fetch-doc (user-path user) (fn [user-data]
                                        (re-frame/dispatch [::events/set-food-items (get user-data :food-items [])])))
        foods (re-frame/subscribe [::subs/foods])]
    (if (nil? @foods)
      [:div "loading food items"]
      [:ul
       (map (fn [f] [:li {:key f} f]) @foods)])))

(defn food-input []
  [:div
   [:input {:type "text"
            :placeholder "Enter food"
            :value @(re-frame/subscribe [::subs/food-input])
            :on-change (fn [e] (re-frame/dispatch [::events/set-food-input (-> e .-target .-value)]))}]
   [:button {:on-click #(re-frame/dispatch [::events/add-food-item])} "Save Food"]])

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        user (re-frame/subscribe [::subs/user])
        loading (re-frame/subscribe [::subs/user-loading?])]
    [:div
     [:h1
      "Hello from " @name]
     (when (and @loading (not @user))
       [:div "Loading..."])
     (if @user
       [:div
        [:div "The logged in user is" @user]
        [food-input]
        [food-list @user]
        [:button {:on-click #(sign-out)}  "Log out"]]
       [:div
        [:div "You are not logged in"]
        [:button {:on-click #(google-sign-in)}  "Login"]])]))
