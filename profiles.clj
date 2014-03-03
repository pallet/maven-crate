{:dev
 {:dependencies [[com.palletops/pallet "0.8.0-RC.7" :classifier "tests"]
                 [com.palletops/crates "0.1.2-SNAPSHOT"]
                 [ch.qos.logback/logback-classic "1.0.9"]]
  :plugins [[lein-set-version "0.3.0"]
            [lein-resource "0.3.2"]
            [com.palletops/lein-pallet-crate "0.1.0"]
            [com.palletops/pallet-lein "0.6.0-beta.8"]]
  :aliases {"live-test-up"
            ["pallet" "up" "--phases" "install,verify" "--roles" "live-test"]
            "live-test-down"
            ["pallet" "down" "--roles" "live-test"]
            "live-test"
            ["do" "live-test-up," "live-test-down"]}
  :test-selectors {:default (complement :live-test)
                   :live-test :live-test
                   :all (constantly true)}}
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
 :vmfest {:dependencies [[com.palletops/pallet-vmfest "0.3.0-RC.1"]]}
 :jclouds-ec2 {:dependencies
               [ [org.cloudhoist/pallet-jclouds "1.5.2"]
                 [org.jclouds.provider/aws-ec2 "1.5.5"]
                 [org.jclouds.provider/aws-s3 "1.5.5"]
                 [org.jclouds.driver/jclouds-slf4j "1.5.5"]
                 [org.jclouds.driver/jclouds-sshj "1.5.5"]]}
 :pallet-ec2 {:dependencies [[com.palletops/pallet-aws "0.1.2-SNAPSHOT"]
                             [org.slf4j/jcl-over-slf4j "1.7.5"]]}}
