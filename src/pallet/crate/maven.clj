(ns pallet.crate.maven
  (:require
   [pallet.actions :as actions]
   [pallet.compute :refer [os-hierarchy]]
   [pallet.crate :as crate]
   [pallet.crate.package.jpackage :as jpackage]
   [pallet.crate-install :as install]
   [pallet.node :as node]
   [pallet.utils :refer [apply-map]]
   [pallet.version-dispatch :as version]))


#_(def maven-parameters
 {:maven-home "/opt/maven2"
  :version "3.0.3"})

#_(defn maven-download-md5
  [version]
  {"2.2.1" "c581a15cb0001d9b771ad6df7c8156f8"
   "3.0.3" "507828d328eb3735103c0492443ef0f0"})


#_(defn maven-download-url
  [version]
  (let [major (first
               (clojure.string/split version #"\."))]
    (format (:download-url-template *defaults*) major version version)))


;; TODO: this needs automated testing because the maven urls change
;; every now and then (toni Oct 2012)
#_(crate/defplan download
  [& {:keys [maven-home version]
              :or {maven-home "/opt/maven2" version "3.0.3"}
              :as options}]
  (actions/remote-directory
   maven-home
   :url (maven-download-url version)
   :md5 (maven-download-md5 version)
   :unpack :tar :tar-options "xz"))


#_(crate/defplan package
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


(def ^:dynamic *defaults*
  {:package-name "maven2"
   :jpackage-releasever "5.0"
   ;; download urls have this format:
   ;; http://mirrors.ibiblio.org/apache/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.tar.gz
   :download-url-template
   "http://mirrors.ibiblio.org/apache/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz"
   ;; md5 urls have this format:
   ;; http://www.apache.org/dist/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz.md5
   :md5-url-template
   "http://www.apache.org/dist/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz.md5"
   :install-dir "/opt/maven2"
   :version "3.2.1"})

(defn default [k]
  (let [k (if (keyword? k) k (keyword k))]
    (when-not (some #{k} (keys *defaults*))
      (throw
       (ex-info
        {:type :internal
         :message
         (format "The default %s does not exist in *defaults*" k)})))
    (k *defaults*)))

(defn maven-url
  "Builds a maven url given a url 'format-style' template and a version number.
  The url format takes 3 parameters: major version, version and
  version.

  - template: the format
    e.g.:
      http://mirrors.ibiblio.org/apache/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz
  - version: the version number of maven
    e.g.: 3.2.1"
  [template version]
  (let [major (first
               (clojure.string/split version #"\."))]
    (format template major version version)))


(crate/defplan archive-settings
  "Settings for when installing from archive"
  [version]
  {:install-strategy :archive
   :install-dir (default :install-dir)
   :install-source
   {:url (maven-url (default :download-url-template) version)
    :md5-url (maven-url (default :md5-url-template) version)
    :unpack :tar
    :tar-options "xz"}})

(crate/defplan jpackage-settings
  "Settings for when installing via jpackage"
  []
  {:install-strategy :package-source
   :repository {:id :jpackage :releasever (default :jpackage-releasever)}
   :packages [(default :package-name)]})

(version/defmulti-version-plan package-settings [os os-version version])

(version/defmethod-version-plan package-settings
  {:os :amzn-linux}
  [_ _ _]
  (jpackage-settings ))

(version/defmethod-version-plan package-settings
  {:os :centos :os-version [5]}
  [_ _ _]
  (jpackage-settings))

(version/defmethod-version-plan package-settings
  {:os :linux}
  [_ _ _]
  {:install-strategy :packages
   :packages [(:package-name *defaults*)]})

(version/defmulti-version-plan archive-settings [os os-version version])

(version/defmethod-version-plan archive-settings
  {:os :linux}
  [_ _ version]
  (archive-settings version))

(crate/defplan settings [s {:keys [instance-id]}]
  (crate/assoc-settings :maven s {:instance-id instance-id}))

(crate/defplan install [& {:keys [instance-id]}]
  (install/install :maven instance-id))
