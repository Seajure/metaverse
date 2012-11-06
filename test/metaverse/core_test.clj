(ns metaverse.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [meta.verse :as v]
            [meta.verse.seed :as seed]))

(defn clear [f]
  (doseq [n ['sample.a.b3c57239c7035149a511ff1baa067c52a948e254
             'sample.a.fd869f8b8cee8667e5a5b51487f87aa5d608f8d4
             'slam.hound.stitch.1a98e2021313105a7e3c7dcd5be578caa812b347
             'slam.hound.stitch.df185e59448b09c0985cbdaf167146cfd4e4df73]]
    (remove-ns n))
  (require 'metaverse.requirer 'metaverse.nser :reload)
  (f))

(defn reseed [f]
  (doseq [f (rest (file-seq (io/file seed/home)))]
    (io/delete-file f))
  (seed/add (io/file "sample/a1.clj"))
  (seed/add (io/file "sample/a2.clj"))
  (seed/-main "slamhound" "slamhound" "1.2.0")
  (seed/-main "slamhound" "slamhound" "1.1.1")
  (seed/-main "slamhound" "slamhound" "1.0.0")
  (f))

(use-fixtures :each clear reseed)

(deftest requirer-test
  (is (= [:original :alternate] ((resolve 'metaverse.requirer/get-abcs)))))

(deftest ns-test
  (is (= [:original :alternate] ((resolve 'metaverse.nser/get-abcs)))))

(deftest test-stitch
  (v/require ['slam.hound.stitch :as 'first :reload true
              :rev "1a98e2021313105a7e3c7dcd5be578caa812b347"])
  (v/require ['slam.hound.stitch :as 'second :reload true
              :rev "df185e59448b09c0985cbdaf167146cfd4e4df73"])
  (let [f (resolve 'first/get-package)
        s (resolve 'second/get-package)]
    (is (nil? f))
    ;; this was added in 1.2.0
    (is (instance? clojure.lang.Var s))))