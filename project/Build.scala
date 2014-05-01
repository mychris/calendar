import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {


  lazy val standardSettings = Seq(
    scalaVersion := "2.10.3",
    routesImport ++= Seq(
      "util.Binders._",
      "com.github.nscala_time.time.Imports._"
    )
  )

  lazy val testSettings = Seq[Setting[_]](
    libraryDependencies ++= Libs.testDependencies
  )

//  def customLessEntryPoints(base: File): PathFinder = (
//    (base / "app" / "assets" / "stylesheets" * "main.less")
//  )

  lazy val calendar = play.Project(
    "calendar",
    "0.1-SNAPSHOT",
    Libs.appDependencies,
    path = file(".")
  )
//  .settings(libraryDependencies += "play" %% "play-test" % "2.2.2" % "it")//from Play 2.2.x on the organization is com.typesafe.play
  .settings(standardSettings:_*)
  .settings(testSettings:_*)
//  .settings(
//    lessEntryPoints := Nil,
//    lessEntryPoints in Compile <<= baseDirectory(customLessEntryPoints)
//  )
}

/** Third party library dependencies */
object Libs {
  val appDependencies = Seq(
    jdbc,
    cache,
    "com.typesafe.akka" % "akka-actor_2.10" % "2.2.4",
    "com.typesafe.akka" % "akka-testkit_2.10" % "2.2.4",
    "com.typesafe.akka" % "akka-slf4j_2.10" % "2.2.4",
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe.slick" % "slick_2.10" % "2.0.1",
    "joda-time" % "joda-time" % "2.3",
    "com.github.nscala-time" %% "nscala-time" % "1.0.0",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
  )

  val testDependencies = Seq(
    "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"
  )
}
