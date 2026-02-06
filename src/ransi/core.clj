(ns ransi.core)

(def style-codes
  {:fx/reset 0
   :fx/bold 1
   :fx/faint 2
   :fx/italic 3
   :fx/underline 4
   :fx/reverse 7
   :fx/conceal 8
   :fx/strike 9
   :fx/double-underline 21
   :fx/normal 22
   :fx/noitalic 23
   :fx/nounderline 24
   :fx/noreverse 27
   :fx/reveal 28
   :fx/nostrike 29})

(def foreground-codes
  {:fg/black 30
   :fg/red 31
   :fg/green 32
   :fg/yellow 33
   :fg/blue 34
   :fg/magenta 35
   :fg/cyan 36
   :fg/white 37
   ;; 38 rgb
   :fg/default 39})

(def background-codes
  {:bg/black 40
   :bg/red 41
   :bg/green 42
   :bg/yellow 43
   :bg/blue 44
   :bg/magenta 45
   :bg/cyan 46
   :bg/white 47
   ;; 48 rgb
   :bg/default 49})

(def style? (set (keys style-codes)))
(def foreground? (set (keys foreground-codes)))
(def background? (set (keys background-codes)))

(def get-ansi (merge style-codes foreground-codes background-codes))
(def ansi? (set (concat style? foreground? background?)))

(defn antonym [code]
  (cond
    (foreground? code) :fg/default
    (background? code) :bg/default
    (style? code) (case code
                    :fx/bold :fx/normal
                    :fx/faint :fx/normal
                    :fx/italic :fx/noitalic
                    :fx/underline :fx/nounderline
                    :fx/double-underline :fx/nounderline
                    :fx/reverse :fx/noreverse
                    :fx/conceal :fx/reveal
                    :fx/strike :fx/nostrike)
    :else :fx/reset))

(defn find-antonym [stack]
  (let [code (peek stack)]
    (if-let [pred (cond (foreground? code) foreground?
                        (background? code) background?)]
      (or (some pred (rseq (pop stack)))
          (antonym code))
      (antonym code))))

(defn ansi [code]
  (str "[" (get-ansi code) "m"))

(defn ransi-form? [x]
  (and (vector? x)
       (ansi? (first x))))

(defn render [hiccup]
  (let [stack (atom [])
        sb (StringBuilder.)]
    (letfn [(build [x]
              (cond
                (ransi-form? x)
                (let [[code & s] x]
                  (.append sb (ansi code))
                  (swap! stack conj code)
                  
                  (build s)
                  
                  (.append sb (ansi (find-antonym @stack)))
                  (swap! stack pop))
                
                (vector? x) (run! build x)
                (list? x) (run! build x)
                (seq? x) (run! build x)
                (#{:br :newline} x) (.append sb "\n")
                (string? x) (.append sb x)))]
      (build hiccup)
      (.toString sb))))
