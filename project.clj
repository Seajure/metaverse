(defproject metaverse "0.1.0-SNAPSHOT"
  :description "Parallel universes for namespaces"
  :url "https://github.com/Seajure/metaverse"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.cemerick/pomegranate "0.0.13"
                  :exclusions [org.slf4j/slf4j-api]]]
  :profiles {:dev {:source-paths ["sample-src" "sample-src2"]}})
