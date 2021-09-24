(ns luissantos.game)

(def previous-move (atom nil))

(defn safe-println [& more]
  (.write *out* (str (clojure.string/join " " more) "\n")))

(defn root
  ""
  [request]
  {
   "apiversion" "1"
   "author" "MyUsername"
   "color"  "#888888"
   "head"  "default"
   "tail"  "default"
   "version"  "0.0.1-beta"
   })


(defn start
  ""
  [game]
  (safe-println "=========== New Game =================")
  #_(println "start" game))


(comment

  (= (next-move {:x 2 :y 6} :up) {:x 2 :y 7})
  (= (next-move {:x 2 :y 6} :down) {:x 2 :y 5})
  (= (next-move {:x 2 :y 6} :left) {:x 1 :y 6})
  (= (next-move {:x 2 :y 6} :up) {:x 3 :y 6})

)

(defn next-move
  ""
  [position move]
  (let [{:keys [x y]} position]
    (case move
      :up    {:x x :y (+ y 1)}
      :down  {:x x :y (- y 1)}
      :right {:x (+ x 1) :y y}
      :left  {:x (- x 1 ) :y y})))

(comment
  (crash? [{:y 5 :x 5} {:y 4 :x 4 } {:y 4 :x 5}] {:y 6 :x 5} )

  (crash? [{:y 3, :x 3} {:y 2, :x 3} {:y 2, :x 4} {:y 1, :x 4} {:y 1, :x 4}] (next-move {:x 3 :y 3} :down ) )
)

(defn crash?
  [body position]
  (->> body
       (some #(= position %))
       (boolean)))



(comment

  (valid-move? 11 11 {:x 0 :y 0})
  (valid-move? 11 11 {:x 10 :y 10})

  (valid-move? 11 11 {:x 11 :y 0})
  (valid-move? 11 11 {:x 0 :y 11})
  (valid-move? 11 11 {:x -1 :y 0})
  (valid-move? 11 11 {:x 0 :y -1})


  (valid-move? [{:y 3, :x 3} {:y 2, :x 3} {:y 2, :x 4} {:y 1, :x 4} {:y 1, :x 4}]
               11 11
               (next-move {:x 3 :y 3} :down )
               )
  )


(defn snake?
  [snakes position]
  (-> (some #(crash? (:body %) position) snakes)
      (boolean)))

(defn valid-move?
  [snakes height width {:keys [x y] :as position}]
  (and
   (>= x 0)
   (>= y 0)
   (< x width)
   (< y height)
   (not (snake? snakes position))))




(comment

  (random-valid-move 11 11 {:x 5 :y 5})

  (next-move {:y 5 :x 6} :right)

  (valid-move? [] 7 7 {:x 7 :y 5} )

  )



(defn random-valid-move
  [{:keys [board]} height width position]
  (let [snakes (:snakes board)
        moves (shuffle [:up :left :down :right])
        possible (map (fn [m] {:move m :pos (next-move position m)}) moves)
        valid (filter #(valid-move? snakes height width (:pos %)) possible)]
    (when-not (empty? valid)
       (:move (first valid)))))


(defn move
  ""
  [game]

  (let [{:keys [height width]}  (:board game)
        you  (:you game)
        head (:head (:you game))
        body (:body (:you game))
        move (random-valid-move game height width (:head (:you game)))]

    (safe-println "id" (:id you) "head" head "move" move)

    (when-not move
      (safe-println "id" (:id you) "you" you)
        )

    #_(reset! move)
    {:move
     (name (if move move :up) )
     }))


(defn end
  ""
  [game]
  #_(println "end" game))
