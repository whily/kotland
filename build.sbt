organization := "net.whily"

name := "scaland"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.4"

scalacOptions ++= Seq("-optimize", "-deprecation")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
