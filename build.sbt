import NativePackagerKeys._

herokuAppName in Compile := "floating-dusk-5648"

name := """DreamSpace"""

libraryDependencies	+=	"mysql"	% "mysql-connector-java"	% "5.1.27"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

fork in run := true

fork in run := true

fork in run := true