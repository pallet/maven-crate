(ns pallet.crate.maven.support-test
  (:require
   [clojure.test :refer :all]
   [pallet.actions :refer [exec-checked-script exec-script* minimal-packages
                           package-manager]]
   [pallet.api :refer [converge group-spec plan-fn]]
   [pallet.build-actions :refer [build-actions build-session]]
   [pallet.core.api :refer [phase-errors]]
   [pallet.core.session :refer [with-session]]
   [pallet.crate :refer [is-64bit?]]
   [pallet.crate.automated-admin-user :refer [automated-admin-user]]
   [pallet.crate.java :as java]
   [pallet.crate.maven :as maven]
   [pallet.crates.test-nodes :as test-nodes]
   [pallet.script :refer [with-script-context]]
   [pallet.script-test :refer [is-true testing-script]]
   [pallet.script.lib :refer [package-manager-non-interactive]]
   [pallet.test-env :refer [teardown test-env *compute-service* *node-spec-meta*]]
   [pallet.test-env.project :as project]))

(test-env test-nodes/node-specs project/project {:threads 10})

(deftest ^:support default-settings
  (let [spec (group-spec
                 (keyword (str "pallet-maven-" (name (:selector *node-spec-meta*))))
               :node-spec (:node-spec *node-spec-meta*)
               :count 1
               :phases
               {:bootstrap (plan-fn
                               (minimal-packages)
                             (package-manager :update)
                             (automated-admin-user))
                :settings (plan-fn
                            (java/settings {})
                            (maven/settings
                             (maven/archive-settings [3 2 1])
                             {}))
                :configure (plan-fn
                             (java/install {})
                             (maven/install {}))
                :verify (plan-fn
                            (exec-script*
                             (testing-script
                              "Maven installed"
                              (is-true ("mvn" -version "2>/dev/null")
                                       "Verify maven is installed"))))})]

    (try
      (let [session (converge spec
                              :phase [:configure :verify]
                              :compute *compute-service*)]
        (testing "install maven"
          (is session)
          (is (not (phase-errors session)))))
      (finally
        (teardown
         (converge (assoc spec :count 0) :compute *compute-service*))
        ))))
