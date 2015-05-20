import NativePackagerKeys._

herokuAppName in Compile := "floating-dusk-5648"

name := """DreamSpace"""

libraryDependencies	+=	"mysql"	% "mysql-connector-java"	% "5.1.27"

libraryDependencies += "com.drewnoakes" % "metadata-extractor" % "2.8.1"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

