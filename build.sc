import mill._, scalalib._
import mill.eval.Result

object fork extends ScalaModule with NativeImageModule {
  def scalaVersion = "2.12.10"
  def ivyDeps = Agg(
    ivy"com.monovore::decline:1.0.0",
    ivy"com.lihaoyi::os-lib:0.3.0",
    ivy"com.lihaoyi::fansi:0.2.7",
  )
  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.7.1"
    )
    def testFrameworks = List("utest.runner.Framework")
  }
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
    )
    finalMainClass()
  }
}
