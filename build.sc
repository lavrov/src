import mill._, scalalib._
import mill.eval.Result

object cli extends ScalaModule with PublishModule with NativeImageModule {
  def scalaVersion = "2.13.3"
  def ivyDeps = Agg(
    ivy"com.monovore::decline:1.0.0",
    ivy"com.lihaoyi::os-lib:0.3.0",
    ivy"com.lihaoyi::fansi:0.2.7",
    ivy"com.lihaoyi::upickle:0.9.9",
  )
  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.7.1"
    )
    def testFrameworks = List("utest.runner.Framework")
  }

  import mill.scalalib.publish._

  def publishVersion = T.input {
    T.ctx.env.get("VERSION") match {
      case Some(version) => Result.Success(version)
      case None => Result.Failure("VERSION env variable is undefined")
    }
  }

  def sonatypeUri = "https://maven.pkg.github.com/lavrov/src"

  def pomSettings = PomSettings(
    description = "Src",
    organization = "com.github.lavrov.src",
    url = "https://github.com/lavrov/src",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("lavrov", "src"),
    developers = Seq(
      Developer("lavrov", "Vitaly Lavrov","https://github.com/lavrov")
    )
  )
}

trait NativeImageModule extends ScalaModule {
  private def javaHome = T.input {
    T.ctx().env.get("JAVA_HOME") match {
      case Some(homePath) => Result.Success(os.Path(homePath))
      case None => Result.Failure("JAVA_HOME env variable is undefined")
    }
  }

  private def nativeImagePath = T.input {
    val path = javaHome()/"bin"/"native-image"
    if (os exists path) Result.Success(path)
    else Result.Failure(
      "native-image is not found in java home directory.\n" +
      "Make sure JAVA_HOME points to GraalVM JDK and " +
      "native-image is set up (https://www.graalvm.org/docs/reference-manual/native-image/)"
    )
  }

  def nativeImage = T {
    import ammonite.ops._
    implicit val workingDirectory = T.ctx().dest
    %%(
      nativeImagePath(),
      "-jar", assembly().path,
      "--no-fallback",
      "--initialize-at-build-time=scala.runtime.Statics$VM",
    )
    finalMainClass()
  }
}
