(ns meta.verse
  (:refer-clojure :exclude [load require ns])
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [meta.verse.seed :as seed])
  (:import (java.security MessageDigest)
           (clojure.lang LineNumberingPushbackReader)))

(defn qualify [lib rev]
  ;; TODO: different separator?
  (symbol (str lib "." rev)))

(defn transform [ns-form rev]
  (let [transformed-ns (qualify (second ns-form) rev)]
    `(clojure.core/ns ~transformed-ns ~@(drop 2 ns-form))))

(defn read-body [reader filename]
  (let [forms (repeatedly #(read reader false ::eof))]
    (map #(vary-meta % assoc :file filename)
         (take-while #(not= ::eof %) forms))))

(defn load [rev]
  (binding [*ns* (find-ns 'user)]
    (let [file (io/file seed/home rev)
          reader (LineNumberingPushbackReader. (io/reader file))
          ns-form (read reader)]
      (eval (transform ns-form rev))
      ;; must read after ns form has been evaluated
      (doseq [form (read-body reader (str file))]
        (eval form)))))

(defn require [[lib & {:as opts} :as orig-args]]
  (if-let [rev (:rev opts)]
    (let [qualified-lib (qualify lib rev)]
      ;; no need to support :reload with immutable namespaces
      (when-not (contains? @@#'clojure.core/*loaded-libs* qualified-lib)
        (load rev))
      (when-let [as (:as opts)]
        (ns-unalias *ns* as)
        (alias as qualified-lib))
      (when-let [refer (:refer opts)]
        (if (= :all refer)
          (refer qualified-lib)
          (apply refer qualified-lib :only refer)))
      (dosync
       (commute @#'clojure.core/*loaded-libs* conj qualified-lib))
      qualified-lib)
    (clojure.core/require orig-args)))

(defn- require? [[clause-type & _]]
  (= clause-type :require))

(defn- versioned-require [[_ & subclauses]]
  (for [subclause subclauses]
    `(require '~subclause)))

(defmacro ns- [name & clauses]
  (let [[requires others] ((juxt filter remove) require? clauses)]
    `(do (clojure.core/ns ~name ~@others)
         ~@(mapcat versioned-require requires))))
