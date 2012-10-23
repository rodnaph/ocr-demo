
(ns ocr-demo.core
  (:use [clojure.java.io :only [file]]
        [clojure.string :only [join split]]
        [clojure.java.shell :only (sh)]))

(def data-dir "data/training")

;;
;; Converting image commands
;;

(color2bw "data/test/2_original.png" "2_bw.png")

(defn color2bw [in out]
  (sh "convert" in "-colorspace" "gray" "+dither" "-colors" "4"
      "-threshold" "33%"
      "-normalize" "-resize" "32x32!" out))

(bw2txt "2_bw.png" "2_data")

(defn bw2txt [in out]
  (let [txt (format "%s.txt" out)]
    (sh "convert" in txt)))

;;
;; Loading data from txt files
;;

(defn to-data [acc c]
  (condp = c
    \0 (conj acc 0)
    \1 (conj acc 1)
    acc))

(defn ^{:doc "Load data file from path"}
  path2data [path]
  (reduce to-data [] (slurp path)))

(defn file2digit [f]
  (subs (.getName f) 0 1))

(defn to-training-data [acc f]
  (let [path (.getAbsolutePath f)]
    (cons [(file2digit f)
           (path2data path)] acc)))

(defn files-in [dir]
  (remove #(.isDirectory %)
    (file-seq (file dir))))

(defn to-color [line] 
  (if (re-find #"255,255,255" line) 0 1))

;;
;; Maths
;;

(defn euclidean-distance [x y]
  (let [squares (map (comp #(* % %) -) x y)]
    (Math/sqrt (reduce + 0 squares))))

(defn data-distance [digit-data [digit data]]
  [digit (euclidean-distance digit-data data)])

;;
;; Useful things
;;

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

(defn guess-digit [digit-data k]
  (->> (training-data)
       (map (partial data-distance digit-data))
       (sort-by second)
       (take k)
       (map first)
       (frequencies)))

