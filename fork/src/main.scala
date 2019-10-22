import com.monovore.decline.CommandApp
import com.monovore.decline.Command
import com.monovore.decline.Opts

object Main extends CommandApp(
  name = "fork",
  header = "GitHub assistant",
  main = {
    val urlOpt = Opts.argument[String]("url")
    
    Opts.subcommand("clone", "Clone remote repository")(urlOpt).map { url =>
      Git.Url.parse(url) match {
        case Some(Git.Url(server, path)) =>
          val shortPath =  if (path endsWith ".git") path.dropRight(4) else path
          os.proc("git", "clone", url, shortPath).call(os.home/"workspace"/server)
        case None =>
          Console.err.println(s"Bad url '$url'")
      }
    }
  }
)
