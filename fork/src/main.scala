import com.monovore.decline.CommandApp
import com.monovore.decline.Command
import com.monovore.decline.Opts

object Main extends CommandApp(
  name = "fork",
  header = "GitHub assistant",
  main = {
    val urlOpt = Opts.argument[String]("url")
    
    Opts.subcommand("clone", "Clone remote repository")(urlOpt).map { url =>
      os.proc("git", "clone", url).call(os.home/"workspace"/"github.com"/"lavrov")
    }
  }
)
