
(ns ocr-demo.core
  (:use ocr-demo.data))

(defn euclidean-distance [x y]
  (let [squares (map (comp #(* % %) -) x y)]
    (Math/sqrt (reduce + 0 squares))))

(defn data-distance [digit-data [digit data]]
  [digit (euclidean-distance digit-data data)])

(defn guess-digit [digit-data k]
  (->> (training-data)
       (map (partial data-distance digit-data))
       (sort-by second)
       (take k)
       (map first)
       (frequencies)))

