import sbt.ScriptedPlugin._

versionWithGit

// library
// The library of PGP functions.
// Note:  We're going to just publish this to the sbt repo now.
lazy val library =
  Project("library", file("gpg-library"))
    .settings(
      name := "pgp-library",
      libraryDependencies ++= Seq(bouncyCastlePgp, gigahorseOkhttp,
        specs2 % Test, sbtIo % Test),
      libraryDependencies ++= {
        scalaBinaryVersion.value match {
          case "2.10" => Nil
          case _      => Seq(parserCombinators)
        }
      }
    )

// The sbt plugin.
lazy val plugin =
  Project("plugin", file("pgp-plugin"))
    .dependsOn(library)
    .settings(
      sbtPlugin := true,
      name := "sbt-pgp",
      libraryDependencies += gigahorseOkhttp,
      libraryDependencies ++= {
        (sbtBinaryVersion in pluginCrossBuild).value match {
          case "0.13" => Seq(sbtCoreNext.value)
          case _      => Nil
        }
      },
      publishLocal := publishLocal.dependsOn(publishLocal in library).value
    )
    // .settings(websiteSettings:_*)
    .settings(scriptedSettings:_*)
    .settings(
      scriptedLaunchOpts += s"-Dproject.version=${version.value}"
    )

// Website settings

enablePlugins(GhpagesPlugin, JekyllPlugin, SiteScaladocPlugin)

git.remoteRepo := "git@github.com:sbt/sbt-pgp.git"

siteSubdirName in SiteScaladoc in library := "library/latest/api"

siteSubdirName in SiteScaladoc in plugin := "plugin/latest/api"

// Release settings
Release.settings

// Disable publishing of root
publish := ()

publishLocal := ()
