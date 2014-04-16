organization := "com.qifun.immutable-future"

name := "adapter"

version := "0.1-SNAPSHOT"

libraryDependencies <+= version { "com.qifun" %% "immutable-future" % _ }

scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature")

scalacOptions in Test ++= Seq("-Yrangepos")

description := "Test cases for immutable-future."

homepage := Some(url("http://github.com/Atry/immutable-future-test"))

startYear := Some(2014)

licenses +=("Scala license", url("https://github.com/scala/async/blob/master/LICENSE"))

pomIncludeRepository := { _ => false }

scmInfo := Some(ScmInfo(
  url("https://github.com/Atry/immutable-future-test"),
  "scm:git:git://github.com/Atry/immutable-future-test.git",
  Some("scm:git:git@github.com:Atry/immutable-future-test.git")))

pomExtra :=
  <developers>
    <developer>
      <id>phaller</id>
      <name>Philipp Haller</name>
      <timezone>+1</timezone>
      <url>http://github.com/phaller</url>
    </developer>
    <developer>
      <id>retronym</id>
      <name>Jason Zaugg</name>
      <timezone>+1</timezone>
      <url>http://github.com/retronym</url>
    </developer>
    <developer>
      <id>Atry</id>
      <name>杨博</name>
      <timezone>+8</timezone>
      <email>pop.atry@gmail.com</email>
    </developer>
  </developers>
