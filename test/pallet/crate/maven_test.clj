(ns pallet.crate.maven-test
  (:use
   clojure.test
   pallet.test-utils)
  (:require
   [pallet.api :as api]
   [pallet.build-actions :as build-actions]
   [pallet.actions :as actions]
   [pallet.crate.automated-admin-user :as automated-admin-user]
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

(def maven-test-spec
  (api/server-spec
   :phases
   {:configure (api/plan-fn
                (maven/package))
    :verify (api/plan-fn
             (actions/exec-checked-script
              "check mvn command exists"
              ("mvn" -version)))}))
