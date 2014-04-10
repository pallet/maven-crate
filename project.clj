(defproject com.palletops/maven-crate "0.8.0-SNAPSHOT"
  :description "Pallet crate to install, configure and use maven"
  :url "http://palletops.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:url "git@github.com:pallet/maven-crate.git"}

  :dependencies [[org.clojure/clojure "1.5.1" :scope "provided"]
                 [com.palletops/pallet "0.8.0-RC.9" :scope "provided"]]
  :resource {:resource-paths ["doc-src"]
             :target-path "target/classes/pallet_crate/maven_crate/"
             :includes [#"doc-src/USAGE.*"]}
  :prep-tasks ["resource" "crate-doc"]
  :jvm-opts ["-Djava.awt.headless=true"])
