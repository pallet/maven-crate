{:dev
 {:dependencies [[com.palletops/pallet "0.8.0-RC.7" :classifier "tests"]
                 [com.palletops/java-crate "0.8.0-beta.6"]
                 [com.palletops/pallet-test-env "RELEASE"]
                 [com.palletops/crates "0.1.2-SNAPSHOT"]
                 [ch.qos.logback/logback-classic "1.0.9"]]
  :plugins [[lein-set-version "0.3.0"]
            [lein-resource "0.3.2"]
            [com.palletops/lein-pallet-crate "0.1.0"]
            [com.palletops/lein-test-env "RELEASE"]
            [com.palletops/pallet-lein "0.8.0-alpha.1"]]}
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
 :jclouds {:pallet/test-env
           {:test-specs
            [{:selector :ubuntu-13-10}
             {:selector :ubuntu-12-04}
             {:selector :amzn-linux-2013-092}
             {:selector :centos-6-5}
             {:selector :debian-7-4}
             {:selector :debian-6-0}]}}
 :aws {:pallet/test-env
       {:test-specs
        [{:selector :ubuntu-13-10}
         ;; {:selector :ubuntu-13-04
         ;;  :expected [{:feature ["oracle-java-8"]
         ;;              :expected? :not-supported}]}
         {:selector :ubuntu-12-04}
         {:selector :amzn-linux-2013-092}
         {:selector :centos-6-5}
         {:selector :debian-7-4}
         {:selector :debian-6-0}]}}
 :vmfest {:pallet/test-env {:test-specs
                            [{:selector :ubuntu-13-04}]}}}
