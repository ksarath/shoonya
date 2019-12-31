import sbt._
import sbt.Keys._

val shoonyaVersion    = "0.0.1-SNAPSHOT"
val SilencerVersion = "1.4.4"

val zioV          = "1.0.0-RC17"
val fs2V          = "2.1.0"
val http4sV       = "0.21.0-M6"
val circeV        = "0.12.2"
val doobieV       = "0.8.8"
val flywayV       = "6.1.3"
val h2V           = "1.4.200"
val postgresV     = "42.2.9"

val baseDeps = Seq(
  "dev.zio"                         %% "zio"                                    % zioV,
  "co.fs2"                          %% "fs2-core"                               % fs2V,
  "co.fs2"                          %% "fs2-io"                                 % fs2V,
  "co.fs2"                          %% "fs2-reactive-streams"                   % fs2V,
  "org.http4s"                      %% "http4s-dsl"                             % http4sV,
  "org.http4s"                      %% "http4s-blaze-server"                    % http4sV,
  "org.http4s"                      %% "http4s-blaze-client"                    % http4sV,
  "org.http4s"                      %% "http4s-client"                          % http4sV,
  "org.http4s"                      %% "http4s-dropwizard-metrics"              % http4sV,
  "org.http4s"                      %% "http4s-circe"                           % http4sV,
  "org.tpolecat"                    %% "doobie-core"                            % doobieV,
  "org.tpolecat"                    %% "doobie-h2"                              % doobieV,
  "org.tpolecat"                    %% "doobie-hikari"                          % doobieV,
  "org.flywaydb"                    %  "flyway-core"                            % flywayV,
  "io.circe"                        %% "circe-generic"                          % circeV,
  "io.circe"                        %% "circe-literal"                          % circeV,
  "io.circe"                        %% "circe-generic-extras"                   % circeV,
  "org.postgresql"                  %  "postgresql"                             % postgresV,
  "com.h2database"                  %  "h2"                                     % h2V,
  "org.slf4j"                       % "slf4j-log4j12"                           % "1.7.26",
  "com.github.pureconfig"           %% "pureconfig"                             % "0.12.1",
  "com.lihaoyi"                     %% "sourcecode"                             % "0.1.7",
  "com.typesafe.scala-logging"      %% "scala-logging"                          % "3.9.2",
  "ch.qos.logback"                  %  "logback-classic"                        % "1.2.3",
  "org.slf4j"                       %  "slf4j-api"                              % "1.7.29",
  "org.scalatest"                   %% "scalatest"                              % "3.0.8"       % Test,
  "org.http4s"                      %% "http4s-testing"                         % http4sV       % Test,
  "com.h2database"                  %  "h2"                                     % h2V           % Test,
  "com.h2database"                  %  "h2"                                     % h2V           % IntegrationTest
)

val _scalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-explaintypes",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:existentials",
  "-Xfatal-warnings",
  "-Xlint:-infer-any,_",
  "-Ymacro-annotations",
  "-Ywarn-value-discard",
  "-Ywarn-numeric-widen",
  "-Ywarn-extra-implicit",
  "-Ywarn-unused:_",
  "-opt:l:inline"
)

val _compilerPlugins = Seq(
  // plugins
  ("com.github.ghik" % "silencer-lib" % SilencerVersion % Provided).cross(CrossVersion.full),
  compilerPlugin(("com.github.ghik" % "silencer-plugin" % SilencerVersion).cross(CrossVersion.full)),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  compilerPlugin(("org.typelevel" % "kind-projector" % "0.11.0").cross(CrossVersion.full))
)

inThisBuild(
  List(
    organization := "org.shoonya",
    version := shoonyaVersion,
    homepage := Some(url("https://ksarath.github.io/shoonya/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "ksarath",
        "Sarath Kallatt Sivasankaran",
        "sarathksivasankaran@gmail.com",
        url("https://ksarath.github.io/ksarath/")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc"),
    scmInfo := Some(
      ScmInfo(url("https://github.com/ksarath/shoonya/"), "scm:git:git@github.com:ksarath/shoonya.git")
    )
  )
)

ThisBuild / publishTo := sonatypePublishToBundle.value

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias(
  "check",
  "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck"
)

lazy val root = (project in file("."))
  .settings(
    name := "shoonya"
  )
  .aggregate(dataTypeModeler)

lazy val dataTypeModeler = (project in file("data-type-modeler"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "data-type-modeler",
    scalaVersion := "2.13.1",
    scalacOptions := _scalacOptions,
    libraryDependencies ++= baseDeps ++ _compilerPlugins,
    fork in (run) := true
  )

lazy val docs = project
  .in(file("shoonya-docs"))
  .settings(
    skip.in(publish) := true,
    moduleName := "shoonya-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(root),
    target in (ScalaUnidoc, unidoc) := (baseDirectory in LocalRootProject).value / "website" / "static" / "api",
    cleanFiles += (target in (ScalaUnidoc, unidoc)).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(unidoc in Compile).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(unidoc in Compile).value
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
