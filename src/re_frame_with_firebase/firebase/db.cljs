(ns re-frame-with-firebase.firebase.db
  (:require  ["firebase/firestore/lite" :as firestore]
             [promesa.core :as p]))

(defn user-path [user]
  (str "users/" (:uid user)))

(defn get-doc [path]
  (firestore/doc (firestore/getFirestore) path))

(defn store-doc [path data]
  (firestore/setDoc (get-doc path) (clj->js data)))

(defn update-doc [path data]
  (firestore/updateDoc (get-doc path) (clj->js data)))

(defn fetch-doc [path onsuccess]
  (-> (firestore/getDoc (get-doc path))
      (p/then (fn [doc]
                (let [data (js->clj (.data doc) :keywordize-keys true)]
                  (onsuccess data))))))

(comment
  (store-doc "users/1" {:test "data"})
  (update-doc "users/1" {:test "data 2"})
  (fetch-doc "users/1" #(println %)))