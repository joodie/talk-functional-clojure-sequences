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
;;;; (c) 2011-2012 Joost Diepenmaat
;;;;     Zeekat Softwareontwikkeling
;;;;
;;;;   http://joost.zeekat.nl/
;;;;   joost@zeekat.nl

(ns user)

;;;;
;;;; Clojure basics that you need to know
;;;

(def my-keyword :a-keyword)

(def my-vector [:a :b :c 4 "some text"])
(my-vector 2)

(def my-map {:one 1
             :two 2
             :sentence "some sentence"})
(my-map :one)
(:one my-map)

(defn my-function
  [arg1 arg2]
  (+ (* arg1 arg2) 10))

(my-function 4 5)

(defn my-other-function
  [arg1 arg2]
  (arg1 (* 4 arg2) 10))

(my-other-function + 5)

;;;;
;;;; Some data
;;;;

(def items [{:name "car"
             :wheels [:wheel1 :wheel2
                      :wheel3 :wheel4]}
            {:name "bike"
             :wheels [:bicycle-wheel-1
                      :bicycle-wheel-2]}
            {:name "tricycle"
             :wheels [:tricycle-wheel1
                      :tricycle-wheel2
                      :tricycle-wheel2]}
            {:name "unicycle"
             :wheels [:unicycle-wheel]}
            {:name "dog"
             :legs [:dog-leg1 :dog-leg2
                    :dog-leg3 :dog-leg4]}])



;;;;
;;;; What is a seq
;;;;

(seq? items) ; => false


;;;;
;;;; Clojure lists are seqs, but vectors, arrays, hash-maps any many
;;;; other things are not
;;;;

(seq? (list 1 2 3 4)) ; => true

(seq? [1 2 3 4]) ; => false

(seq? {:a 1 :b 2}) ; => false

;;;;
;;;;  You can *make* a seq from many things
;;;;

(seq items) ; => ({:name "car", :wheels [:wheel1 :wheel2 :wheel3 :wheel4]} {:name "bike", :wheels [:bicycle-wheel-1 :bicycle-wheel-2]} {:name "tricycle", :wheels [:tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2]} {:name "unicycle", :wheels [:unicycle-wheel]} {:name "dog", :legs [:dog-leg1 :dog-leg2 :dog-leg3 :dog-leg4]})

(seq? (seq items)) ; => true

(seq {:name "value"
      :stuff 2}) ; => ([:name "value"] [:stuff 2])

(seq #{:a :b 1 2}) ; => (1 2 :a :b)

;;;;
;;;; So what is a seq?
;;;;

;;;;
;;;; What is a list?
;;;;

;;;;  Cell     Cell     Cell     Cell
;;;;  +-+-+    +-+-+    +-+-+    +-+-+
;;;;  |*|*+--> |*|*+--> |*|*+--> |*|*+--> ..
;;;;  +++-+    +++-+    +++-+    +++-+
;;;;   |        |        |        |
;;;;   V        V        V        V
;;;;  value    value    value    value

(def v1 :my-value)
(def v2 :another-value)
(def lst (list 1 2 3 4))

(cons v1 lst) ; => (:my-value 1 2 3 4)

(cons v1 (cons v2 lst)) ; => (:my-value :another-value 1 2 3 4)


;;;;
;;;; Seqs are *abstractions* that act like immutable linked lists
;;;;

;;;;  Cell     Cell     Cell     Cell
;;;;  +-+-+    +-+-+    +-+-+    +-+-+
;;;;  |*|*+--> |*|*+--> |*|*+--> |*|*+--> ..
;;;;  +++-+    +++-+    +++-+    +++-+
;;;;   |        |        |        |
;;;;   V        V        V        V
;;;;  value    value    value    value


;;;;
;;;; What does a seq provide
;;;;

(def short-list (list :a :b :c))

(first short-list) ; => :a
(rest short-list) ; => (:b :c)
(next short-list)  ; => (:b :c)
(cons :d short-list) ; => (:d :a :b :c)

(rest '(:a)) ; => ()
(next '(:a)) ; => nil

(next ()) ; => nil
(rest ()) ; => ()

;; But do not think too hard about rest vs next right now
;; when in doubt, use next


;;;;
;;;; Seq to collections
;;;;

(def s (seq [1 2 3 4 5 6])) ; => #'user/s

;; Direct constructors

(vec s) ; => [1 2 3 4 5 6]
(set s) ; => #{1 2 3 4 5 6}

;; conj into an existing collection

(into [] s) ; => [1 2 3 4 5 6]
(into #{} s) ; => #{1 2 3 4 5 6}
(into {} (seq [[:a 1] [:b 2]])) ; => {:a 1, :b 2}

;; but note:
(into '() s) ; => (6 5 4 3 2 1)


;;;;
;;;; `seq` on empty collections returns nil
;;;;

(seq? ())

(seq nil) ; => nil
(seq ()) ; => nil
(seq []) ; => nil
(seq {}) ; => nil
(seq #{}) ; => nil


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
            {:name "tricycle"
             :wheels [:tricycle-wheel1
                      :tricycle-wheel2
                      :tricycle-wheel2]}
            {:name "unicycle"
             :wheels [:unicycle-wheel]}
            {:name "dog"
             :legs [:dog-leg1 :dog-leg2
                    :dog-leg3 :dog-leg4]}])

;;;;
;;;; what does this do?
;;;;

((fn self [coll]
   (and (seq coll)
        (cons (:name (first coll))
              (self (next coll)))))
 items) ; => ("car" "bike" "tricycle" "unicycle" "dog")


;;;;
;;;; what does this do?
;;;;

((fn self [coll]
   (and (seq coll)
        (cons (:name (first coll))
              (self (next coll)))))
 items)

;;;; seems like a lot of boiler plate
;;;; when we just want to extract the names

;;;;
;;;; solution: take a function as an argument
;;;;

((fn self [f coll]
   (and (seq coll)
        (cons (f (first coll))
              (self f (next coll)))))
 (fn [i] (:name i)) ; f argument
 items) ; => ("car" "bike" "tricycle" "unicycle" "dog")


;;;;
;;;; solution: take a function as an argument
;;;;

((fn self [f coll]
   (and (seq coll)
        (cons (f (first coll))
              (self f (next coll)))))
 (fn [i] (:name i))
 items)

;; problem:
;; still uses stack space proportional to (count coll)


;;;;
;;;; solution: use lazy-seq.
;;;;

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)


;;;;
;;;; solution: use lazy-seq.
;;;;

((fn self [f coll]
   (lazy-seq (and (seq coll)
                  (cons (f (first coll))
                        (self f (next coll))))))
 (fn [i] (:name i))
 items)

;;;; which is basically, map:

(map (fn [item]
       (:name item))
     items) ; => ("car" "bike" "tricycle" "unicycle" "dog")


;;;;
;;;; solution: use lazy-seq.
;;;;

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


;;;;
;;;; solution: use lazy-seq.
;;;;

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
(:key {:key :value}) ; => :value
(map :name items)


;;;;
;;;; What else can we do with seqs
;;;;

;; get all the things with wheels:

(filter :wheels items) ; => ({:name "car", :wheels [:wheel1 :wheel2 :wheel3 :wheel4]} {:name "bike", :wheels [:bicycle-wheel-1 :bicycle-wheel-2]} {:name "tricycle", :wheels [:tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2]} {:name "unicycle", :wheels [:unicycle-wheel]})

;; or

(keep :wheels items) ; => ([:wheel1 :wheel2 :wheel3 :wheel4] [:bicycle-wheel-1 :bicycle-wheel-2] [:tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2] [:unicycle-wheel])

;;;; get all the thing WITHOUT wheels:

(remove :wheels items) ; => ({:name "dog", :legs [:dog-leg1 :dog-leg2 :dog-leg3 :dog-leg4]})


;;;;
;;;; get all the wheels as a single seq:
;;;;

(mapcat :wheels items) ; => (:wheel1 :wheel2 :wheel3 :wheel4 :bicycle-wheel-1 :bicycle-wheel-2 :tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2 :unicycle-wheel)


;;;;
;;;; get everything with more than 2 wheels:
;;;;

(filter #(< 2 (count (:wheels %))) items) ; => ({:name "car", :wheels [:wheel1 :wheel2 :wheel3 :wheel4]} {:name "tricycle", :wheels [:tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2]})


;;;;
;;;; sort items by number of wheels (ascending)
;;;;

(sort [6 2 8 10]) ; => (2 6 8 10)

(defn wheel-count
  [item]
  (count (:wheels item)))

(sort-by wheel-count items) ; => ({:name "dog", :legs [:dog-leg1 :dog-leg2 :dog-leg3 :dog-leg4]} {:name "unicycle", :wheels [:unicycle-wheel]} {:name "bike", :wheels [:bicycle-wheel-1 :bicycle-wheel-2]} {:name "tricycle", :wheels [:tricycle-wheel1 :tricycle-wheel2 :tricycle-wheel2]} {:name "car", :wheels [:wheel1 :wheel2 :wheel3 :wheel4]})


;;;;
;;;; sort descending
;;;;

(defn reverse-compare
  [a b]
  (compare b a))

(sort-by wheel-count reverse-compare items)


;;;;
;;;; some infinite sequences
;;;;

(take 10 (repeatedly rand)) ; => (0.6121773318782444 0.19423236761108165 0.8850590126241661 0.2166941331273856 0.9595494895081473 0.5251935455837357 0.3684493851601529 0.5340716398033004 0.9248327915320852 0.7105046471052063)

(take 10 (repeat 4)) ; => (4 4 4 4 4 4 4 4 4 4)

(take 10 (iterate inc 0)) ; => (0 1 2 3 4 5 6 7 8 9)

(take 10 (cycle [1 2 3 4])) ; => (1 2 3 4 1 2 3 4 1 2)

;; and ranges

(range 10) ; => (0 1 2 3 4 5 6 7 8 9)

(range 10 0 -1) ; => (10 9 8 7 6 5 4 3 2 1)


;;;;
;;;; Summarize a seq:
;;;;
;;;; Turn a seq into a "single value"
;;;;


;;;;
;;;; does every item have legs?
;;;;

(every? :legs items) ; note: predicate names end with "?"


;;;;
;;;; are there things with legs?
;;;;

(some :legs items) ; => [:dog-leg1 :dog-leg2 :dog-leg3 :dog-leg4]

;;;;
;;;; also..
;;;;

(not-every? :legs items) ; => true

(not-any? :antennea items) ; => true


;;;;
;;;; count the wheels. note: (count nil) = 0
;;;;

(reduce + 3 [1 2 3 4]) ; => 13

(reduce conj () (list 1 2 3 4)) ; => (4 3 2 1)

(reduce #(+ %1 (count (:wheels %2))) 0 items)

;; or

(reduce + (map #(count (:wheels %)) items))

;; or

(reduce + (map count (map :wheels items)))

;; or

(reduce + (map wheel-count items))


;;;;
;;;; Some notes
;;;;

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
