(ns pallet.crate.maven
  (:require
   [pallet.actions :as actions]
   [pallet.api :as api]
   [pallet.compute :refer [os-hierarchy]]
   [pallet.crate :as crate]
   [pallet.crate.package.jpackage :as jpackage]
   [pallet.crate-install :as install]
   [pallet.node :as node]
   [pallet.utils :refer [apply-map]]
   [pallet.version-dispatch :as version]))


(def ^:dynamic *defaults*
  {:package-name {2 "maven2"
                  3 "maven3"}
   :jpackage-releasever "6.0"
   ;; download urls have this format:
   ;; http://mirrors.ibiblio.org/apache/maven/maven-2/2.2.1/binaries/apache-maven-2.2.1-bin.tar.gz
   :download-url-template
   "http://mirrors.ibiblio.org/apache/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz"
   ;; md5 urls have this format:
   ;; http://www.apache.org/dist/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz.md5
   :md5-url-template
   "http://www.apache.org/dist/maven/maven-%s/%s/binaries/apache-maven-%s-bin.tar.gz.md5"
   :install-dir-template "/opt/maven%s"
   })

(defn default [k]
  (let [k (if (keyword? k) k (keyword k))]
    (when-not (some #{k} (keys *defaults*))
      (throw
       (ex-info  (format "The default %s does not exist in *defaults*" k)
        {:type :internal })))
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



(crate/defplan jpackage-settings
  "Settings for when installing via jpackage"
  [package-name]
  {:install-strategy :package-source
   :repository {:id :jpackage :releasever (default :jpackage-releasever)}
   :packages [package-name]})

(version/defmulti-version-plan package-settings [version])

(version/defmethod-version-plan package-settings
  {:os :amzn-linux}
  [_ _ [maj _ _]]
  (jpackage-settings (get (default :package-name) maj)))

(version/defmethod-version-plan package-settings
  {:os :centos :os-version [5]}
  [_ _ [maj _ _]]
  (jpackage-settings (get (default :package-name) maj)))

(version/defmethod-version-plan package-settings
  {:os :linux}
  [_ _ [maj _ _]]
  {:install-strategy :packages
   :packages [(get (default :package-name) maj)]})

(version/defmethod-version-plan package-settings
  {:os :debian :version [3]}
  [_ os-version _]
  {:install-strategy :package-source
   :repository {:id :debian-backports}
   :packages ["maven3"]})

(version/defmethod-version-plan package-settings
  {:os :ubuntu :os-version [13]}
  [_ _ _]
  (throw (ex-info
          "Installing maven via packages is not yet supported on Ubuntu 13+"
          {:type :feature-not-available
           :crate :maven})))

(version/defmethod-version-plan package-settings
  {:os :ubuntu :version [3]}
  [_ _ _]
  {:install-strategy :package-source
   :package-source {:apt {:url "ppa:natecarlson/maven3"}
                    :name "maven3"}
   :packages ["maven3"]
   :link ["/usr/bin/mvn3" "/usr/bin/mvn"]})

(version/defmethod-version-plan package-settings
  {:os :centos :version [3]}
  [_ _ [maj _ _]]
  (throw (ex-info
          "Installing maven via packages is not supported for CentOS"
          {:type :feature-not-available
           :crate :maven}))
  ;; this is broken. The package installs correctly, but a logging
  ;; config file is missing.
  #_{:install-strategy :package-source
   :package-source {:yum { :url "http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-$releasever/$basearch/"
                          :enabled "1"}
                    :name "epel-maven"}
   :packages ["apache-maven"]})

(version/defmulti-version-plan archive-settings [version])

(version/defmethod-version-plan archive-settings
  {:os :linux}
  [_ _ [maj min p]]
  (let [version (apply str (clojure.string/join "." [maj min p]))
        install-dir (format (default :install-dir-template) maj)]
    {:install-strategy :archive
     :install-dir install-dir
     :install-source
     {:url (maven-url (default :download-url-template) version)
      :md5-url (maven-url (default :md5-url-template) version)
      :unpack :tar
      :tar-options "xz"}
     :link [(format "%s/bin/mvn" install-dir)
            "/usr/bin/mvn"]}))

(crate/defplan settings [s options]
  (crate/assoc-settings :maven s options))

(crate/defplan install [ {:keys [instance-id]}]
  (install/install :maven instance-id)
  ;; create a link for maven when needed
  (when-let [link (:link (crate/get-settings :maven))]
    (let [[from to] link]
      (actions/symbolic-link from to))))

(defn server-spec
  "Default spec for installing maven from archive.

  - settings: 
   Use `archive-settings` to create the settings for installing from archive, and
   `package-settings` for installing from package.
  - options: nil for now.
"
  [settings & {:keys [instance-id] :as options}]
  (api/server-spec
   :phases
   {:settings (api/plan-fn
               (crate/assoc-settings :maven settings options))
    :install (api/plan-fn (install options))}
   :default-phases [:install]))
