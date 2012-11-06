(ns meta.verse.seed
  (:require [cemerick.pomegranate.aether :as aether]
            [clojure.java.io :as io])
  (:import (java.util.jar JarFile JarEntry)
           (java.security MessageDigest)
           (java.io File InputStream ByteArrayOutputStream)))

(def repos [["central" {:url "http://repo1.maven.org/maven2/"}]
            ["clojars" {:url "https://clojars.org/repo/"}]])

(def home (io/file (System/getProperty "user.home") ".metaverse"))

(defn sha1 [bytes]
  (let [digest (.digest (MessageDigest/getInstance "SHA1") bytes)]
    (format "%x" (BigInteger. 1 digest))))

(defmulti add class)

(defmethod add (class (.getBytes "")) [bytes]
  (let [checksum (sha1 bytes)
        out-file (io/file home checksum)]
    (.mkdirs home)
    (when-not (.exists out-file)
      (io/copy bytes out-file))))

(defmethod add File [f]
  (cond (.isDirectory f)
        (doseq [f (file-seq (io/file f))
                :when (.isFile f)]
          (add f))

        (.endsWith (str f) "jar")
        (add (JarFile. f))

        :else
        (add (io/input-stream f))))

(defmethod add JarFile [f]
  (doseq [entry (enumeration-seq (.entries f))]
    (add (.getInputStream f entry))))

(defmethod add java.io.InputStream [is]
  (add (let [baos (ByteArrayOutputStream.)]
         (io/copy is baos)
         (.toByteArray baos))))

(defn -main
  ([] (add (io/file (System/getProperty "user.home") "m2" "repository")))
  ([dir] (add (io/file dir)))
  ([group artifact version]
     (doseq [f (->> [[(symbol group artifact) version]]
                    (aether/resolve-dependencies :repositories repos
                                                 :coordinates)
                    (aether/dependency-files))]
       (add f))))