import mill._, scalalib._, scalanativelib._
import mill.eval.Result

object cli extends ScalaModule
  with ScalaNativeModule
  with PublishModule
{
  def scalaVersion = "2.13.8"
  def scalaNativeVersion = "0.4.3"
  def ivyDeps = Agg(
    ivy"com.monovore::decline::2.2.0",
    ivy"com.lihaoyi::os-lib::0.7.8",
    ivy"com.lihaoyi::fansi::0.3.0",
    ivy"com.lihaoyi::upickle::1.4.2",
  )
  object test extends Tests with TestModule.Utest {
    def ivyDeps = super.ivyDeps() ++ Agg(
      ivy"com.lihaoyi::utest::0.7.10",
    )
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
