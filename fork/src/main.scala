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
          val shortPath =  os.RelPath(if (path endsWith ".git") path.dropRight(4) else path)
          val absPath = os.home/"workspace"/server/shortPath
          os.proc("git", "clone", url, absPath).call()
          Console.out.println(
            TerminalUtil.success(s"Cloned into $absPath")
          )
        case None =>
          Console.err.println(
            TerminalUtil.error(s"Bad url '$url'")
          )
      }
    }
  }
)

object TerminalUtil {
  def success(message: String = "") = inBracket(fansi.Color.Green("Success")) + " " + message
  def error(message: String) = inBracket(fansi.Color.Red("Error")) + " " + message
  private def inBracket(v: fansi.Str) = "[" + v + "]"
}