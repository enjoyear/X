name := "play-app"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions in Global ++= Seq(
  "-encoding", "UTF-8",
  "-source", "1.8",
  "-target", "1.8"
)

scalaVersion := "2.11.7"
//scalaVersion := "2.12.0"
//LAME: You need to manually remove jars in /.m2/ to let sbt see updated installed jars.
//resolvers += "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repository"
resolvers += Resolver.mavenLocal

lazy val scalaTestVersion = "2.2.5"
lazy val testLib = Seq(
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test,it" withSources() withJavadoc()
)

//libraryDependencies ++= testLib

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.apache.commons" % "commons-math3" % "3.5",
  "com.chen.guo" % "X-crawler" % "1.0.1"
)

