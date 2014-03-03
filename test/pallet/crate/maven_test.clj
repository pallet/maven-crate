(ns pallet.crate.maven-test
  (:use
   clojure.test
   pallet.test-utils)
  (:require
   [pallet.api :as api]
   [pallet.actions :as actions]
   [pallet.crate :as crate]
   [pallet.crate.maven :as maven]
   [pallet.script.lib :as lib]))

#_(deftest download-test
  (is (= (first
          (build-actions/build-actions
           {}
           (actions/remote-directory
            "/opt/maven2"
            :url (maven/maven-download-url "2.2.1")
            :md5 (maven/maven-download-md5 "2.2.1")
            :unpack :tar :tar-options "xj"))))
      (first
       (build-actions/build-actions
        {}
        (maven/download :version "2.2.1")))))

(defn maven-test-spec [version-vec]
  (api/server-spec
   :extends []
   :phases
   {:configure (api/plan-fn (maven/install))}))

(defn maven-package-test-spec [version-spec]
  (api/server-spec
   :phases
   {:settings (api/plan-fn
               (maven/settings (maven/package-settings version-spec) {}))
    :install (api/plan-fn (maven/install {}))
    :verify (api/plan-fn
             (actions/exec-checked-script
              "check the 'mvn' command exists"
              ("mvn" -version)))}))

(defn maven-archive-test-spec [version-spec]
  (let [[maj _ _] version-spec]
    (api/server-spec
     :extends [(maven/server-spec version-spec)]
     :phases
     {:verify
      (api/plan-fn
       (let [mvn-path (format "/opt/maven%s/bin/mvn" maj)]
         (actions/exec-checked-script
          (str "Verify mvn exists in " mvn-path)
          (when-not (file-exists? ~mvn-path)
            ("exit" 1)))))})))

