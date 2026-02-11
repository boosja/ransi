(ns terminator.print-perf
  (:require [clojure.pprint :as pp]
            [clojure.string]))

(def number-of-xs 100000)
(def times 100)

(defn get-now []
  (. System (nanoTime)))

(defn get-duration [start]
  (/ (double (- (get-now)
                start))
     1000000.0))

(defmacro mytime
  "Evaluates expr and returns the time it took."
  [expr]
  `(let [start# (get-now)
         ret# ~expr]
     (get-duration start#)))

(defn ->str [t]
  (str "Elapsed time: " (.toPlainString (bigdec t)) " msecs (avg)"))

(defn print-every []
  (dotimes [_ number-of-xs]
    (print "x"))
  (flush))

(defn apply-str-print []
  (print (apply str (repeat number-of-xs "x")))
  (flush))

(defn str-join-print []
  (print (clojure.string/join (repeat number-of-xs "x")))
  (flush))

(defn str-builder-print []
  (let [sb (StringBuilder.)]
    (dotimes [_ number-of-xs]
      (.append sb "x"))
    (print (.toString sb))
    (flush)))

(defn avg-time [f]
  (loop [i times
         sum 0]
    (if (= i 0)
      (double (/ sum times))
      (recur (dec i) (+ sum (mytime (f)))))))

(defn print-perf [& _]
  (let [start (get-now)
        prn-every (avg-time print-every)
        prn-apply (avg-time apply-str-print)
        prn-sjoin (avg-time str-join-print)
        prn-build (avg-time str-builder-print)]
    (println)
    (pp/pprint {:number-of-xs number-of-xs
                :times times
                :total (get-duration start)
                :just-print (->str prn-every)
                :apply-str (->str prn-apply)
                :str-join (->str prn-sjoin)
                :str-builder (->str prn-build)})))

(comment

  ;; CLJ
  ;; make perf-clj (print 1000 xs 10 times)
  {:number-of-xs 1000
   :times 10
   :total 34462.377292
   :just-print "Elapsed time: 48.9237249 msecs (avg)"
   :apply-str "Elapsed time: 381.5216544 msecs (avg)"
   :str-join "Elapsed time: 940.9743249999999 msecs (avg)"
   :str-builder "Elapsed time: 2074.7100458000004 msecs (avg)"}

  ;; Babashka
  ;; make perf-bb (print 1000 xs 10 times)
  {:number-of-xs 1000
   :times 10
   :total 26.722959
   :just-print "Elapsed time: 1.5777329999999998 msecs (avg)"
   :apply-str "Elapsed time: 0.025225299999999996 msecs (avg)"
   :str-join "Elapsed time: 0.025653899999999997 msecs (avg)"
   :str-builder "Elapsed time: 1.0341374 msecs (avg)"}

  ;; make perf-bb (print 1000 xs 10_000 times)
  {:number-of-xs 1000
   :times 10000
   :total 22200.592916
   :just-print "Elapsed time: 1.2822869012999971 msecs (avg)"
   :apply-str "Elapsed time: 0.0286034878999999 msecs (avg)"
   :str-join "Elapsed time: 0.028097219799999924 msecs (avg)"
   :str-builder "Elapsed time: 0.8795433400000003 msecs (avg)"}

  ;; make perf-bb (print 100_000 xs 100 times)
  {:number-of-xs 100000
   :times 100
   :total 22472.005833
   :just-print "Elapsed time: 127.50708169000004 msecs (avg)"
   :apply-str "Elapsed time: 4.459767509999999 msecs (avg)"
   :str-join "Elapsed time: 4.144527120000001 msecs (avg)"
   :str-builder "Elapsed time: 88.59946996999997 msecs (avg)"}

  ;; Babashka is crazy fast!

  )
