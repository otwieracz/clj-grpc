(defproject clj-grpc "0.1.1"
  :description "A Clojure library designed to provide hassle-free, ready to go gRPC experience without ton of preparations and Java code."
  :url "https://github.com/otwieracz/clj-grpc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [camel-snake-kebab "0.4.0"]
                 [com.stuartsierra/component "0.4.0"]
                 [com.google.protobuf/protobuf-java "3.10.0"]
                 ;; grpc java dependencies: https://blog.jmibanez.com/2018/07/22/grpc-with-clojure-and-leiningen.html
                 [io.netty/netty-codec-http2 "4.1.25.Final"]
                 [io.grpc/grpc-core "1.25.0" :exclusions [io.grpc/grpc-api]]
                 [io.grpc/grpc-stub "1.25.0"]
                 [io.grpc/grpc-protobuf "1.25.0"]
                 [io.grpc/grpc-netty "1.25.0" :exclusions [io.netty/netty-codec-http2 io.grpc/grpc-core io.grpc/grpc-api]]]
  :plugins [[org.clojars.awebneck/lein-protoc "0.5.5"]]
  :proto-source-paths ["src/proto"]
  :protoc-version "3.10.0"
  :protoc-grpc {:version "1.25.0"}
  :proto-target-path "target/generated-sources/protobuf"
  :source-paths ["src/clj"]
  :java-source-paths ["target/generated-sources/protobuf"]
  :aot :all
  )
