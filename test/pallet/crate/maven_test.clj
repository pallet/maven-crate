(ns pallet.crate.maven-test
  (:use
   clojure.test
   pallet.test-utils)
  (:require
   [pallet.api :as api]
   [pallet.actions :as actions]
   [pallet.crate :as crate]
   [pallet.crate.maven :as maven]))

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



#_(def maven-test-spec
  (api/server-spec
   :phases
   {:configure (api/plan-fn
                (maven/package))
    :verify (api/plan-fn
             (actions/exec-checked-script
              "check mvn command exists"
              ("mvn" -version)))}))

(crate/defplan verify []
  (actions/exec-checked-script
   "check the 'mvn' command exists"
   ("mvn" -version)))

(def maven-test-spec
  (api/server-spec
   :phases
   {:configure (api/plan-fn (maven/install))
    :verify (api/plan-fn (verify))}))

(def maven-package-test-spec
  (api/server-spec
   :extends [maven-test-spec]
   :phases
   {:settings (api/plan-fn
               (maven/package-settings [3 2 1]))}))

(def maven-archive-test-spec
  (api/server-spec
   :extends [maven-test-spec]
   :phases
   {:settings (api/plan-fn
               (maven/settings (maven/archive-settings "3.2.1")))}))

