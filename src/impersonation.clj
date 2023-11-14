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

