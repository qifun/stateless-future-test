val statelessFuture = Project("stateless-future", file("stateless-future"))

val adapter = Project("adapter", file("adapter")).dependsOn(statelessFuture)

val test = Project("test", file("test")).dependsOn(statelessFuture, adapter)

val root = Project("root", file(".")).aggregate(test)

scalaVersion in ThisBuild := "2.10.4"
