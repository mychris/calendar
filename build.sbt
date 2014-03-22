name := "calendar"

version := "0.1"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.typesafe.akka" % "akka-actor_2.10" % "2.3.0",
  "com.typesafe.akka" % "akka-slf4j_2.10" % "2.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "com.typesafe.slick" % "slick_2.10" % "2.0.1",
  "com.darwinsys" % "hirondelle-date4j" % "1.5.1"
)     

play.Project.playScalaSettings