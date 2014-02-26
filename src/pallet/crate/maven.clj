(ns pallet.crate.maven
  (:require
   [pallet.actions :as actions]
   [pallet.crate :as crate]
   [pallet.crate.package.jpackage :as jpackage]
   [pallet.node :as node]
   [pallet.utils :refer [apply-map]])
  (:use
   pallet.thread-expr))

(def maven-parameters
 {:maven-home "/opt/maven2"
  :version "3.0.3"})

(defn maven-download-md5
  [version]
  {"2.2.1" "c581a15cb0001d9b771ad6df7c8156f8"
   "3.0.3" "507828d328eb3735103c0492443ef0f0"})


(defn maven-download-url
  [version]
  (let [major (first
               (clojure.string/split version #"\."))]
    ;; links are of this format:
    ;; http://mirrors.ibiblio.org/apache/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.tar.gz
    (format "http://mirrors.ibiblio.org/apache/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz"
            major version version)))


;; TODO: this needs automated testing because the maven urls change
;; every now and then (toni Oct 2012)
(crate/defplan download
  [& {:keys [maven-home version]
              :or {maven-home "/opt/maven2" version "3.0.3"}
              :as options}]
  (actions/remote-directory
   maven-home
   :url (maven-download-url version)
   :md5 (maven-download-md5 version)
   :unpack :tar :tar-options "xz"))


(crate/defplan package
  [& {:keys [package-name] :or {package-name "maven2"} :as options}]
  (let [os-family (node/os-family (crate/target-node))
        os-version (node/os-version (crate/target-node))
        use-jpackage (or
                      (= :amzn-linux os-family)
                      (and
                       (= :centos os-family)
                       (re-matches
                        #"5\.[0-5]" os-version)))
        options (if use-jpackage
                  (assoc options
                    :enable ["jpackage-generic" "jpackage-generic-updates"])
                  options)]
    (when use-jpackage
      (jpackage/add-jpackage :releasever "5.0")
      (jpackage/package-manager-update-jpackage)
      (jpackage/jpackage-utils))
    (apply-map actions/package package-name options)))
