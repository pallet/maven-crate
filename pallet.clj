;;; Pallet project configuration file

(require
 '[pallet.crate.maven-test
   :refer [maven-test-spec]]
 '[pallet.crates.test-nodes :refer [node-specs]])

(defproject maven-crate
  :provider node-specs                  ; supported pallet nodes
  :groups [(group-spec "maven-test"
             :extends [with-automated-admin-user maven-test-spec]
             :roles #{:live-test :default :maven})])
