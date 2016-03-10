organization  := "com.xy-inc"
version       := "0.1"
scalaVersion  := "2.11.7"
scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:postfixOps"
)

libraryDependencies ++= {
  val akkaVersion = "2.3.14"
  val sprayVersion = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayVersion,
    "io.spray"            %%  "spray-routing" % sprayVersion,
    "io.spray"            %%  "spray-json"    % sprayVersion,
    "io.spray"            %%  "spray-testkit" % sprayVersion  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaVersion,
    "com.typesafe.akka"   %%  "akka-slf4j"    % akkaVersion,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaVersion   % "test",
    "com.typesafe.slick"  %%  "slick"         % "3.1.1",
    "com.h2database"      %   "h2"            % "1.3.175",
    "org.scalatest"       %%  "scalatest"     % "2.2.4"       % "test",
    "ch.qos.logback"      %   "logback-classic" % "1.1.3"
  )
}

Revolver.settings

fork in run := true

// database tests keep creating and dropping schemas in the same in-memory database so cannot run in parallel
parallelExecution in Test := false
