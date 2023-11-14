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
(def test-message "Hello! This is a test message! :)")


(defn build-markov-chain [words]
  (loop [in words
         out {}]
    (let [current-word (first in)
          next-word (second in)
          current-word-map (get out current-word)]
      (if (nil? current-word-map)
        (recur
         words
         (assoc out current-word {}))
        (if-not (nil? next-word)
          (recur
           (rest in)
           (assoc out
                  current-word
                  (assoc current-word-map next-word (inc (get current-word-map next-word 0)))))
          out)))))

(defn build-messages-markov-chain [messages]
  (build-markov-chain
   (reduce #(concat %1 (-> %2 (normalise-str) (string/split #" ")))
           []
           messages)))
