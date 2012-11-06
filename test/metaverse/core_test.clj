(ns metaverse.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [meta.verse.seed :as seed]))

(defn clear [f]
  (doseq [n ['sample.a.b3c57239c7035149a511ff1baa067c52a948e254
             'sample.a.fd869f8b8cee8667e5a5b51487f87aa5d608f8d4]]
    (remove-ns n))
  (seed/add (io/file "sample/a1.clj"))
  (seed/add (io/file "sample/a2.clj"))
  (require 'metaverse.requirer 'metaverse.nser :reload)
  (f))

(use-fixtures :each clear)

(deftest requirer-test
  (is (= [:original :alternate] ((resolve 'metaverse.requirer/get-abcs)))))

(deftest ns-test
  (is (= [:original :alternate] ((resolve 'metaverse.nser/get-abcs)))))