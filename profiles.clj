{:dev
 {:dependencies [[com.palletops/pallet "0.8.0-RC.7" :classifier "tests"]
                 [com.palletops/crates "0.1.2-SNAPSHOT"]
                 [com.palletops/java-crate "0.8.0-beta.6"]
                 [com.palletops/pallet-test-env "0.1.0"]
                 [ch.qos.logback/logback-classic "1.0.9"]]
  :plugins [[lein-set-version "0.3.0"]
            [lein-resource "0.3.2"]
            [com.palletops/lein-pallet-crate "0.1.0"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]
            [configleaf "0.4.6"]]
  :test-selectors {:default (complement :support)
                   :support :support
                   :all (constantly true)}
  :configleaf {:config-source-path "test"
               :namespace pallet.crate.maven.project
               :verbose true}
  :hooks [configleaf.hooks]}
 :doc {:dependencies [[com.palletops/pallet-codox "0.1.0"]]
       :plugins [[codox/codox.leiningen "0.6.4"]
                 [lein-marginalia "0.7.1"]]
       :codox {:writer codox-md.writer/write-docs
               :output-dir "doc/0.8/api"
               :src-dir-uri "https://github.com/pallet/maven-crate/blob/develop"
               :src-linenum-anchor-prefix "L"}
       :aliases {"marg" ["marg" "-d" "doc/0.8/annotated"]
                 "codox" ["doc"]
                 "doc" ["do" "codox," "marg"]}}
 :release
 {:set-version
  {:updates [{:path "README.md" :no-snapshot true}]}}
 :jclouds {:dependencies [[com.palletops/pallet-jclouds "1.7.0-alpha.2"]
                          [org.apache.jclouds.driver/jclouds-slf4j "1.7.1"
                           :exclusions [org.slf4j/slf4j-api]]
                          [org.apache.jclouds.driver/jclouds-sshj "1.7.1"]]
           :pallet/test-env {:service :aws
                             :test-specs
                             [{:selector :ubuntu-13-04}
                              {:selector :ubuntu-13-10}]}}
 :aws {:dependencies [[com.palletops/pallet-aws "0.2.0"]
                      [ch.qos.logback/logback-classic "1.1.1"]
                      [org.slf4j/jcl-over-slf4j "1.7.6"]]
       :pallet/test-env {:service :ec2
                         :test-specs
                         [ ;; {:selector :ubuntu-13-10}
                          ;; {:selector :ubuntu-13-04
                          ;;  :expected [{:feature ["oracle-java-8"]
                          ;;              :expected? :not-supported}]}
                          ;; {:selector :ubuntu-12-04}
                          {:selector :amzn-linux-2013-092}
                          ;; {:selector :centos-6-5}
                          ;; {:selector :debian-7-4}
                          ;; {:selector :debian-6-0}
                          ]}}
 :vmfest {:dependencies [[com.palletops/pallet-vmfest "0.4.0-alpha.1"]]
          :pallet/test-env {:service :vmfest
                            :test-specs
                            [{:selector :ubuntu-13-04}]}}}
