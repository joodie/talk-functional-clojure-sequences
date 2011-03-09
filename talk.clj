;;;;
;;;;  ____________________
;;;; < Functional Clojure >
;;;;  --------------------
;;;;         \   ^__^
;;;;          \  (oo)\_______
;;;;             (__)\       )\/\
;;;;                 ||----w |
;;;;                 ||     ||
;;;;
;;;; Functional clojure: sequences
;;;;
;;;; (c) 2011 Joost Diepenmaat
;;;;     Zeekat Softwareontwikkeling
;;;;
;;;;   http://joost.zeekat.nl/
;;;;   joost@zeekat.nl

(ns user)

;;;; Some data

(def items [{:name "car"
             :wheels [:wheel1 :wheel2
                      :wheel3 :wheel4]}
            {:name "bike"
             :wheels [:bicycle-wheel-1
                      :bicycle-wheel-2]}
            {:name "reliant"
             :wheels [:reliant-wheel1
                      :reliant-wheel2
                      :reliant-wheel2]}
            {:name "unicycle"
             :wheels [:unicycle-wheel]}
            {:name "dog"
             :legs [:dog-leg1 :dog-leg2
                    :dog-leg3 :dog-leg4]}])

;;;; What is a seq

(seq? items)


;;;;
;;;; Clojure lists are seqs, but vectors, arrays, hash-maps any many
;;;; other things are not
;;;;

(seq? (list 1 2 3 4))
(seq? [1 2 3 4])
(seq? {:a 1 :b 2})

;;;;  But you can make a seq from many things

(seq items)

(seq? (seq items))

(seq {:name "value"
      :stuff 2})

(seq #{:a :b 1 2})

;;;; Seq on empty collections

(seq? ())

(seq nil)
(seq ())
(seq [])
(seq {})

;; etc...


;;;; What does a seq provide

(def short-list (list :a :b :c))

(first short-list)
(rest short-list)
(cons :d short-list)

(rest '(:a))
(next '(:a))

;; But do not think too hard about rest vs next right now
;; when in doubt, use next


;;;;
;;;; Making a seq from a seq
;;;;

;; reminder:

(def items [{:name "car"
             :wheels [:wheel1 :wheel2
                      :wheel3 :wheel4]}
            {:name "bike"
             :wheels [:bicycle-wheel-1
                      :bicycle-wheel-2]}
            {:name "reliant"
             :wheels [:reliant-wheel1
                      :reliant-wheel2
                      :reliant-wheel2]}
            {:name "unicycle"
             :wheels [:unicycle-wheel]}
            {:name "dog"
             :legs [:dog-leg1 :dog-leg2
                    :dog-leg3 :dog-leg4]}])

;;;; what does this do?

((fn self [coll]
   (and (seq coll)
        (cons (:name (first coll))
              (self (next coll)))))
 items)


;;;; what does this do?

((fn self [coll]
   (and (seq coll)
        (cons (:name (first coll))
              (self (next coll)))))
 items)

;;;; seems like a lot of boiler plate
;;;; when we just want to extract the names

;;;; solution: take a function as an argument

((fn self [f coll]
   (and (seq coll)
        (cons (f (first coll))
              (self f (next coll)))))
 (fn [i] (:name i)) ; f argument
 items) ; coll argument


;;;; solution: take a function as an argument

((fn self [f coll]
   (and (seq coll)
        (cons (f (first coll))
              (self f (next coll)))))
 (fn [i] (:name i))
 items)

;; problem:
;; still uses stack space proportional to (count coll)


;;;; solution: use lazy-seq.

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)


;;;; solution: use lazy-seq.

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)

;;;; which is basically, map:

(map (fn [item]
       (:name item))
     items)


;;;; solution: use lazy-seq.

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)

;;;; which is basically, map:

(map (fn [item]
       (:name item))
     items)

;;;; let's shorten that a bit

(map #(:name %)
     items)


;;;; solution: use lazy-seq.

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)

;;;; which is basically, map:

(map (fn [item]
       (:name item))
     items)

;;;; let's shorten that a bit

(map #(:name %)
     items)

;;;; but keywords are already functions!

(map :name items)


;;;; What else can we do with seqs

;; get all the things with wheels:

(filter :wheels items) ; where (:wheels %) true

;; or

(keep :wheels items)   ; where (:wheels %) != nil

;;;; get all the thing WITHOUT wheels:

(remove :wheels items)


;;;; get all the wheels as a single seq:

(mapcat :wheels items) ; note: output has different length than input


;;;; get everything with more than 2 wheels:

(filter #(< 2 (count (:wheels %))) items)


;;;; sort items by number of wheels (ascending)

(sort [6 2 8 10])

(defn wheel-count
  [item]
  (count (:wheels item)))

(sort-by wheel-count items)


;;;; sort descending

(defn reverse-compare
  [a b]
  (compare b a))

(sort-by wheel-count reverse-compare items)


;;;; some infinite sequences

(take 10 (repeatedly rand))

(take 10 (repeat 4))

(take 10 (iterate inc 0))

(take 10 (cycle [1 2 3 4]))

;; and ranges

(range 10)

(range 10 0 -1)


;;;;
;;;; Summarize a seq:
;;;;
;;;; Turn a seq into a "single value"
;;;;


;;;; does every item have legs?

(every? :legs items) ; note: predicate names end with "?"


;;;; are there things with legs?

(some :legs items) ; returns the first true value of (:legs %) or nil,
                   ; so not a predicate

;;;; also..

(not-every? :legs items)

(not-any? :antennea items)


;;;; count the wheels. note: (count nil) = 0

(reduce #(+ %1 (count (:wheels %2))) 0 items)

;; or

(reduce + (map #(count (:wheels %)) items))

;; or

(reduce + (map count (map :wheels items)))

;; or

(reduce + (map wheel-count items))


;;;; Some notes

;; all of the shown functions are just functions written in pure
;; clojure

;; you can write your own variants and you don't even need macros to
;; do it

;; pmap does map in parallel
;; your performance may vary

;; this presentation is just a clojure source file

;;;;
;;;;  _________
;;;; < The End >
;;;;  ---------
;;;;         \   ^__^
;;;;          \  (oo)\_______
;;;;             (__)\       )\/\
;;;;                 ||----w |
;;;;                 ||     ||
;;;;
;;;; Functional clojure: sequences
;;;;
;;;;   http://joost.zeekat.nl/
;;;;   joost@zeekat.nl


