organization := "com.qifun.stateless-future"

name := "adapter"

version := "0.1.2-SNAPSHOT"

libraryDependencies <+= version { "com.qifun" %% "stateless-future" % _ }

scalacOptions in compile ++= Seq("-optimize", "-deprecation", "-unchecked", "-Xlint", "-feature")

scalacOptions in Test ++= Seq("-Yrangepos")

description := "Test cases for statelesss-future."

homepage := Some(url("http://github.com/Atry/statelesss-future-test"))

startYear := Some(2014)

licenses +=("Scala license", url("https://github.com/scala/async/blob/master/LICENSE"))

pomIncludeRepository := { _ => false }

scmInfo := Some(ScmInfo(
  url("https://github.com/Atry/statelesss-future-test"),
  "scm:git:git://github.com/Atry/statelesss-future-test.git",
  Some("scm:git:git@github.com:Atry/statelesss-future-test.git")))

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
