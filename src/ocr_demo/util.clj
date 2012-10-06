
(ns ocr-demo.util)

;; Public
;; ------

(defn euclidean-distance [x y]
  (let [squares (map (comp #(* % %) -) x y)]
    (Math/sqrt (reduce + 0 squares))))

