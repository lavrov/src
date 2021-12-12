import mill._, scalalib._
import mill.eval.Result

object cli extends ScalaModule with PublishModule {
  def scalaVersion = "2.13.7"
  def ivyDeps = Agg(
    ivy"com.monovore::decline:2.2.0",
    ivy"com.lihaoyi::os-lib:0.7.8",
    ivy"com.lihaoyi::fansi:0.3.0",
    ivy"com.lihaoyi::upickle:1.4.2",
  )
  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.7.10"
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
