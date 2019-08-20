// *****************************************************************************
// Projects
// *****************************************************************************

inThisBuild(
  List(
    crossScalaVersions := List("2.12.9", "2.13.0"),
    scalaVersion := "2.13.0",
    organization := "io.metabookmarks",
    organizationName := "Olivier NOUGUIER",
    startYear := Some(2019),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "cheleb",
        "Olivier NOUGUIER",
        "olivier.nouguier@gmail.com",
        url("https://github.com/cheleb"),
      ),
    ),
  ),
)

lazy val `lagom-scalameta` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++=  Seq(
        library.scalameta,
        library.scalaCheck % Test,
        library.scalaTest % Test,
      ) ++ {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, n)) if n >= 13 => Nil
          case _ =>
            compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)) :: Nil
        }
      }
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val scalaCheck = "1.14.0"
      val scalaTest = "3.0.8"
      val scalameta = "4.2.0"
    }

    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
    val scalameta = "org.scalameta" %% "scalameta" % Version.scalameta
    val playJson = Seq(
      "com.typesafe.play" %% "play-json" % "2.8.0-M5",
      "org.julienrf" %% "play-json-derived-codecs" % "6.0.0"
    )
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
    scalafmtSettings

def crossFlags(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, 13)) =>
    Seq("-Ymacro-annotations")
  case Some((2, _)) =>
    Seq("-Ypartial-unification",
      "-Ywarn-unused-import")

}

def crossPlugins(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, 13)) =>
    Nil
  case Some((2, _)) =>
    Seq(
      compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)),
      compilerPlugin(("org.typelevel" % "kind-projector" % "0.10.1").cross(CrossVersion.binary)))
}

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    scalacOptions ++= Seq(
      //      "-Xplugin-require:macroparadise",
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
    ) ++ crossFlags(scalaVersion.value),
    //addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.allBut(Wart.Any, Wart.Nothing),
    libraryDependencies ++= library.playJson
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
  )

lazy val scalametaSettings =
  Seq()


lazy val examples = project.in(file("examples"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.0-M5")
  .settings(
    libraryDependencies ++= crossPlugins(scalaVersion.value))
  .dependsOn(`lagom-scalameta`)