# clj-grpc

A Clojure library designed to provide hassle-free, ready to go gRPC experience without ton of preparations and Java code...

...or just a bunch of macros.

### Rationale

* Enable easy and fast integration of gRPC into existing projects
* Save user from painful Java-in-Clojure experience
* Only require user to read basic Java tutorial for gRPC (https://grpc.io/docs/tutorials/basic/java/) without necessarily
taking deep dive into all the Java libraries around gRPC, servers like `netty`, what Java class to import where, etc.
* Avoid creating new logic, letting user to follow official Java guide.

### Assumptions
* One gRPC service is implemented per Clojure namespace
* Maybe being too optimistic about `kebab-case` to `camelCase` conversion in some places (weird identifiers might cause 
problems here and there)

### TODO
* Convenient access to request parameters (Tutorial below says nothing about accessing `example_id` from request)
* Implement helpers or (and) document how to use `clj-grpc` without `danielsz/system`
* Tests - unfortunately, there are none (only thin layer of `spec`). But this is just a bunch of macros developed on real project, so I had no time to build
full testing workflow.

### See also (or rather "read before use")
* https://grpc.io/docs/tutorials/basic/java/ - for general gRPC in Java tutorial
* https://github.com/danielsz/system - as `clj-grpc` currently implements only `system` (`component`) behavior.
* https://blog.jmibanez.com/2018/07/22/grpc-with-clojure-and-leiningen.html - for examples of Java-level gRPC in Clojure

### Credits
* @LiaisonTechnologies and @awebneck for `lein-protoc`
* @jmibanez for gRPC introduction in Clojure https://blog.jmibanez.com/2018/07/22/grpc-with-clojure-and-leiningen.html

## Usage

* In `project.clj` add `clj-grpc` to dependencies:
```clojure
[clj-grpc "0.1.0"]
```

* Configure `lein-protoc` plugin. Please note, that until original `protoc` is fixed, for Clojure 1.9 you have to use 
`awebneck` fork. For more details, see https://github.com/LiaisonTechnologies/lein-protoc/pull/16
```clojure
:plugins [...
          [org.clojars.awebneck/lein-protoc "0.5.5"]
          ...
          ]
;; decide which version of protoc and grpc-java to use
:protoc-version "3.10.0"
:protoc-grpc {:version "1.25.0"}
:protoc-source-paths ["src/proto"] ;; where to look for `.proto` files
:proto-target-path "target/generated-sources/protobuf" ;; where should protoc put generated sources
:java-source-paths [... "target/generated-sources/protobuf"] ;; point java compiler to newly generated sources
```

* Create `example.proto` file in `src/protoc`:
```proto
syntax = "proto3";

option java_package = "example.grpc_api";
option java_outer_classname = "ExampleProto";

package example;

message GetAllExamplesParams {
}

message GetExampleParams {
    string example_id = 1;
}


service Example {
    rpc GetExample (GetExampleParams) returns (Example) {
    }

    rpc GetAllExamples (GetAllExamplesParams) returns (stream Example) {
    }
}

message Example {
    string example_id = 1;
    string name = 2;
    string description = 3;
}
```

* Create namespace implementing `grpc` service:
```clojure
(ns example.grpc-api.core
  ;; require macros from `clj-grpc.server` namespace
  (:require [clj-grpc.server :refer [implement-grpc-service defrpc on-next]]))

;; describe service which will be implemented in this namespace.
;; `:java-package` and `:java-outer-classname` has to be consistent with `proto` file
;;
;; NOTE: it might be necessary to restart Clojure process after this is defined so all the classes will be compiled
(implement-grpc-service Client
  :java-package "data_engine.grpc.client"
  :java-outer-classname "ClientProto" )

;; define RPC method `getExample` returning only one example
(defrpc getExample [_this _req res]
  ;;
  ;; do some code here
  ;;
  ;; Finally call `on-next` which builds message of type `Example`, initializes it with provided map
  ;; handling kebab-case to camelCase conversion and sends it through StreamObserver `res`
  (on-next "Example" res
           {:name                  "Name"
            :description           "Description"
            :example-id            "example-123123123"}))

;; Another example of method, this time `server-streaming`.
;; Implemenation has nothing specific about it, just call `on-next` multiple times

(defrpc getAllExamples [_this _req res]
  (dotimes [x 10]
    (on-next "Example" res
           {:name                  (str "Name " x)
            :description           (str "Description" x)
            :example-id            (str "example-" x)})))
```

* Assuming you're using `danielsz/system`, add `GrpcServer` component to your system definition:
```
;; Require `new-grpc-server`
(ns example.systems
  "System definition"
  (:require [com.stuartsierra.component :as component]
            [system.core :refer [defsystem]]
            [clj-grpc.server :refer [new-grpc-server]]
            ...
            )

;; Add `GrpcServer` to system definition. Specify port on which to listen and services, that is sequence of vectors 
;; specifying pairs of:
;; - Clojure namespace, where desired gRPC service is implemented (in our case 
(defsystem dev-system
           [...
            :my-grpc-server (new-grpc-server :port 5000 :services [["data-engine.grpc.client.core" "Client"]])
            ...
            ]
```

Start you system with system's `(start)` and you should have your service running!



## Note for IntelliJ IDEA + Cursive users 
To avoid warnings and unexpected behaviour when using provided macros, configure their IDE resolution as follows:
* `implement-grpc-service` - resolve as `def`
* `defgrpc` - resolve as `defn`

Behavior won't be perfect, but good enough - for example warnings about unused functions might still occur.

See https://cursive-ide.com/userguide/macros.html#customising-symbol-resolution for more info.

## License

Copyright © 2019 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
