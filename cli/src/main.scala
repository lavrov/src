import com.monovore.decline.CommandApp
import com.monovore.decline.Opts

object Main
    extends CommandApp(
      name = "src",
      header = "git assistant",
      main = {
        val urlOpt = Opts.argument[String]("url")

        Opts.subcommand("clone", "Clone remote repository")(urlOpt).map { str =>
          WithConfig { config =>
            Git.Url.parse(str) match {
              case Some(Git.Url(url, server, path)) =>
                val shortPath = os.RelPath(
                  if (path endsWith ".git") path.dropRight(4)
                  else path
                )
                val absPath = os.Path.expandUser(config.workspace) / server / shortPath
                if (os exists absPath) {
                    TerminalUtil.error(s"Destination path already exists $absPath")
                }
                else {
                  try {
                    val exitCode =
                      new ProcessBuilder("git", "clone", url, absPath.toString())
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start()
                        .waitFor()
                    if (exitCode == 0)
                      TerminalUtil.success(s"Cloned into $absPath")
                  }
                  catch {
                    case _: Throwable => TerminalUtil.error("Clone failed")
                  }
                }
              case None =>
                TerminalUtil.error(s"Bad url '$str'")
            }
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

object WithConfig {
  def apply(f: Config => Unit): Unit = {
    val config = Config.read().getOrElse {
      Config.write(Config.default)
      TerminalUtil.warning(s"No config file found. Created default one")
      Config.default
    }
    f(config)
  }
}
