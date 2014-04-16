val immutableFuture = Project("immutable-future", file("immutable-future"))

val adapter = Project("adapter", file("adapter")).dependsOn(immutableFuture)

val test = Project("test", file("test")).dependsOn(immutableFuture, adapter)

val root = Project("root", file(".")).aggregate(test)

scalaVersion in ThisBuild := "2.10.4"
