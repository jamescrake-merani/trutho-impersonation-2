(ns impersonation
  [:require [clojure.string :as string]])

(def match-punc-expr
  "A regular expression which matches against all punctuation"
  #"[!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~]")

(defn normalise-str [in]
  "Remove punctuation, spaces et cetera"
  (-> in
      (string/trim)
      (string/lower-case)
      (string/replace match-punc-expr "")))

;; TODO: For testing purposes. I'll remove this later.
(def test-message "tick tock tick tock tick tock tick tock")

(defn build-markov-chain [words]
  "Returns a map representing a markov chain where each word in WORDS will is a
  key in the map, and the value contains all the frequencies of a next word
  coming."
  (loop [in words
         out {}]
    (let [current-word (first in)
          next-word (second in)
          current-word-map (get out current-word)]
      (if (and (nil? current-word-map) (not (nil? current-word)))
        (recur
         in
         (assoc out current-word {:total 0}))
        (if-not (nil? current-word)
          (recur
           (rest in)
           (assoc out
                  current-word
                  (assoc current-word-map next-word (inc (get current-word-map next-word 0))
                         :total (inc (:total current-word-map)))))
          out)))))

(defn build-messages-markov-chain [messages]
  "Build a map representing a markov chain from MESSAGES."
  (build-markov-chain
   (reduce #(concat %1 (-> %2 (normalise-str) (string/split #" ") (conj nil)))
           []
           messages)))

(defn markov-next-word
  [current chain]
  (let [frequency-map (get chain current)]
    (if (nil? frequency-map)
      nil
      (let [random-pos (rand-int (:total frequency-map))]
        (loop [accumulator 0
               words (seq frequency-map)]
          (if (= (-> words first first) :total)
            (recur
             accumulator
             (rest words))
            (let [current-word (first words)
                  new-accumulator (+ accumulator (second current-word))]
              (if (> new-accumulator random-pos)
                (first current-word)
                (recur new-accumulator
                       (rest words))))))))))

 (defn generate-message [origin chain]
   (loop [words [origin]]
     (let [next-word (markov-next-word (last words) chain)]
       (if (nil? next-word)
         (string/join " " words)
         (recur (conj words next-word))))))
