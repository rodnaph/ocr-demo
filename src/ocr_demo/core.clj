
(ns ocr-demo.core
  (:use ocr-demo.util
        ocr-demo.data))

(defn data-distance [digit-data data]
  {:digit (:digit data)
   :distance (euclidean-distance digit-data (:data data))})

(defn to-score [[digit matches]]
  [digit (count matches)])

(defn guess-digit [digit-data k]
  (->> (training-data)
       (map (partial data-distance digit-data))
       (sort-by :distance)
       (take k)
       (group-by :digit)
       (map to-score)))

