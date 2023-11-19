(ns re-frame-with-firebase.firebase.init
  (:require
   [re-frame.core :refer [dispatch]]
   [re-frame-with-firebase.events :as events]
   [re-frame-with-firebase.firebase.db :refer [store-doc fetch-doc user-path]]
   ["firebase/app" :as firebase]
   ["firebase/firestore/lite" :as firestore]
   ["firebase/auth" :refer [GoogleAuthProvider getAuth signInWithPopup onAuthStateChanged signOut]]))

(defn store-user [user]
  (let [user-to-store (if user {:uid (.-uid user)
                                :name (.-displayName user)} nil)
        user-fb-path (user-path user-to-store)]
    (when user-to-store
      (fetch-doc user-fb-path  (fn [u]
                                 (when (nil? u)
                                   (store-doc user-fb-path {})))))
    (dispatch [::events/set-user user-to-store])))

(defn init []
  (when (zero? (alength (firebase/getApps)))
    (println "initalise firebase")
    (firebase/initializeApp #js {:apiKey ""
                                 :authDomain ""
                                 :projectId ""
                                 :storageBucket ""
                                 :messagingSenderId ""
                                 :appId ""
                                 :measurementId ""})
    (println "initialize firestore")
    (firestore/initializeFirestore (firebase/getApp))
    (onAuthStateChanged (getAuth) (fn [user]
                                    (store-user user)))))


(defn google-sign-in []
  (let [provider (GoogleAuthProvider.)
        auth (getAuth)]
    (signInWithPopup auth provider)))

(defn sign-out []
  (signOut (getAuth)))

(comment
  (google-sign-in)
  (init))

