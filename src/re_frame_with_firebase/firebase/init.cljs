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
    (firebase/initializeApp #js {:apiKey "AIzaSyAdim1cZ9y2QUxOpuCMj2xxVaqgd5RLKCE"
                                 :authDomain "reframe-with-firebase.firebaseapp.com"
                                 :projectId "reframe-with-firebase"
                                 :storageBucket "reframe-with-firebase.appspot.com"
                                 :messagingSenderId "804694848798"
                                 :appId "1:804694848798:web:4ac933184c9bd884afc50f"
                                 :measurementId "G-XX3963MDC1"})
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

