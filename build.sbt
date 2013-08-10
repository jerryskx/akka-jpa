name := "akka-jpa"

organization := "com.skechers"

version := "1.0"

scalaVersion := "2.10.1"

description := "JPA Service with Akka"

//parallelExecution in ThisBuild := false
parallelExecution in Test := false

resolvers ++= Seq(
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"Skechers Artifacts" at "http://ultra:8081/artifactory/skechers",
	"Skechers Artifacts - ext" at "http://ultra:8081/artifactory/ext-release-local",
	"Skechers Artifacts - snapshots" at "http://ultra:8081/artifactory/snapshots",
	"Skechers Artifacts - ext-snapshot" at "http://ultra:8081/artifactory/ext-snapshot-local"
)

libraryDependencies ++= Seq(
    "org.specs2" % "specs2_2.10" % "1.14",
	"com.typesafe.akka" % "akka-actor_2.10" % "2.2.0",
	"com.typesafe.akka" % "akka-file-mailbox_2.10" % "2.2.0",
    "org.hibernate" % "hibernate-entitymanager" % "4.1.2.Final",
//	"com.jolbox" % "bonecp" % "0.8.0-rc1",
//	"com.jolbox" % "bonecp-provider" % "0.8.0-alpha1",
    "com.skechers" % "skechers-commons_2.10" % "1.0.3-SNAPSHOT"
)

//publishTo := Some(Resolver.url("ultra", url("http://ultra.skechers.com:8081/artifactory/skechers"))(Patterns(true, "[organization]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]")))
publishTo :=  Some("Skechers Artifacts - snapshots" at "http://ultra:8081/artifactory/snapshots")

