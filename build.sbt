val statelessFuture = Project("stateless-future", file("stateless-future"))

val statelessFutureAkka = Project("stateless-future-akka", file("stateless-future-akka")).dependsOn(statelessFuture)

val adapter = Project("adapter", file("adapter")).dependsOn(statelessFuture)

val test = Project("test", file("test")).dependsOn(statelessFuture, adapter, statelessFutureAkka)

val root = Project("root", file(".")).aggregate(test)

scalaVersion in ThisBuild := "2.10.4"

crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.0")
