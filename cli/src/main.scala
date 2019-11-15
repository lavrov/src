import com.monovore.decline.CommandApp
import com.monovore.decline.Command
import com.monovore.decline.Opts

object Main
    extends CommandApp(
      name = "src",
      header = "git assistant",
      main = {
        val urlOpt = Opts.argument[String]("url")

        Opts.subcommand("clone", "Clone remote repository")(urlOpt).map {
          url =>
            Git.Url.parse(url) match {
              case Some(Git.Url(server, path)) =>
                val shortPath = os.RelPath(
                  if (path endsWith ".git") path.dropRight(4)
                  else path
                )
                Config.workspacePath match {
                  // TODO: improve error handling
                  case Left(e) => TerminalUtil.error(s"Can't get workspace path: $e")
                  case Right(workspace) =>
                    val absPath = workspace / server / shortPath
                    if (os exists absPath) {
                        TerminalUtil.error(s"Destination path already exists $absPath")
                    }
                    else {
                      os.proc("git", "clone", url, absPath).call()
                      TerminalUtil.success(s"Cloned into $absPath")
                    }
              }
              case None =>
                TerminalUtil.error(s"Bad url '$url'")
            }
        }
      }
    )

object TerminalUtil {
  def warning(message: String) = Console.out.println(
    s"${inBracket(fansi.Color.Yellow("Warning"))} $message"
  )
  def success(message: String = "") = Console.out.println(
    inBracket(fansi.Color.Green("Success")) + " " + message
  )
  def error(message: String) = Console.err.println(
    inBracket(fansi.Color.Red("Error")) + " " + message
  )
  private def inBracket(v: fansi.Str) = "[" + v + "]"
}
