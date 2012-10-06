
(ns ocr-demo.data
  (:use [clojure.java.io :only [file]]
        [clojure.string :only [join split]]))

(def data-dir "data/training")

(defn- to-data [acc c]
  (condp = c
    \0 (conj acc 0)
    \1 (conj acc 1)
    acc))

(defn ^{:doc "Load data file from path"}
  path2data [path]
  (reduce to-data [] (slurp path)))

(defn- file2digit [f]
  (subs (.getName f) 0 1))

(defn- to-training-data [acc f]
  (let [path (.getAbsolutePath f)]
    (cons [(file2digit f)
           (path2data path)] acc)))

(defn- files-in [dir]
  (remove #(.isDirectory %)
    (file-seq (file dir))))

(defn- to-color [line] 
  (if (re-find #"255,255,255" line) 0 1))

;; Public
;; ------

(defn ^{:doc "Loads the training data, returning a map
  of characters to vectors of vectors of data"}
  training-data
  ([] (training-data data-dir))
  ([path]
    (reduce to-training-data {}
      (files-in path))))

(defn ^{:doc "convert ImageMagick test data to
  pure text data for saving to data file."}
  txt2data [path]
  (->> (split (slurp path) #"\n")
       (map to-color)))

