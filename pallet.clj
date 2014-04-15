;;; Pallet project configuration file

(require
 '[pallet.crate.maven-test
   :refer [maven-package-test-spec
           maven-archive-test-spec]]
 '[pallet.crates.test-nodes :refer [node-specs]])

(defproject maven-crate
  :provider node-specs                  ; supported pallet nodes
  :groups [;; test package install of maven version 3.x.y
           (group-spec "maven-package-test-v3"
             :extends [with-automated-admin-user (maven-package-test-spec [3])]
             :roles #{:live-test :default :maven :package :package-3})
           ;; test package install of maven version 2.x.y
           (group-spec "maven-package-test-v2"
             :extends [with-automated-admin-user (maven-package-test-spec [2])]
             :roles #{:live-test :default :maven :package :package-2})
           ;; test archive install
           (group-spec "maven-archive-test"
             :extends [with-automated-admin-user (maven-archive-test-spec [3 2 1])]
             :roles #{:live-test :default :maven :archive})])
