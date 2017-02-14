name := "forecast-io-scala"

organization := "com.film42"

version := "0.0.1"

scalaVersion := "2.12.1"

resolvers += "spray" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "0.12.0",
  "io.spray" %%  "spray-json" % "1.3.3",
  "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.1",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "commons-io" % "commons-io" % "2.4"
)

initialCommands := "import com.film42.forecastioapi._"

