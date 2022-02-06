import mill._, scalalib._, scalanativelib._
import mill.api.Result

object cli extends Module {
  object jvm extends CliModule {
    object test extends Tests with CliTestModule
  }
  object native extends CliModule with ScalaNativeModule {
    def scalaNativeVersion = "0.4.3"
    object test extends Tests with CliTestModule
  }
}

trait CliModule extends ScalaModule with PublishModule {
  def scalaVersion = "2.13.8"
  def millSourcePath = super.millSourcePath / _root_.os.up
  def ivyDeps = Agg(
    ivy"com.monovore::decline::2.2.0",
    ivy"com.lihaoyi::os-lib::0.7.8",
    ivy"com.lihaoyi::fansi::0.3.0",
    ivy"com.lihaoyi::upickle::1.4.2",
  )

  import mill.scalalib.publish._

  def artifactName = "cli"

  def publishVersion = T.input {
    T.ctx.env.get("VERSION") match {
      case Some(version) => Result.Success(version)
      case None => Result.Failure("VERSION env variable is undefined")
    }
  }

  def sonatypeUri: String = "https://s01.oss.sonatype.org/service/local"

  def pomSettings = PomSettings(
    description = "Src",
    organization = "io.github.lavrov.src",
    url = "https://github.com/lavrov/src",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("lavrov", "src"),
    developers = Seq(
      Developer("lavrov", "Vitaly Lavrov","https://github.com/lavrov")
    )
  )
}

trait CliTestModule extends TestModule with TestModule.Utest {
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"com.lihaoyi::utest::0.7.10",
  )
}
