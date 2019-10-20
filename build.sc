import mill._, scalalib._
import mill.eval.Result

object fork extends ScalaModule with NativeImageModule {
  def scalaVersion = "2.12.10"
  def ivyDeps = Agg(
    ivy"com.monovore::decline:1.0.0",
    ivy"com.lihaoyi::os-lib:0.3.0",
  )
}

trait NativeImageModule extends ScalaModule {
  private def graalVmHome = T.input {
    T.ctx().env.get("GRAALVM_HOME") match {
      case Some(homePath) => Result.Success(homePath)
      case None => Result.Failure("GRAALVM_HOME env variable is undefined")
    }
  }

  def nativeImage = T {
    import ammonite.ops._
    implicit val workingDirectory = T.ctx().dest
    val graalHome = graalVmHome()
    val command = s"$graalHome/bin/native-image"
    val commandResult =
      %%(command,
        "-jar", assembly().path,
        "--no-fallback",
      )
    finalMainClass()
  }
}
