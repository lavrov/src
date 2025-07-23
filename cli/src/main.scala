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
                    SelfLogger.error(s"Destination path already exists $absPath")
                }
                else {
                  try {
                    val process = new ProcessBuilder("git", "clone", url, absPath.toString()).start()
                    val outputLines = io.Source.fromInputStream(process.getInputStream).getLines()
                    val errorLines = io.Source.fromInputStream(process.getErrorStream).getLines()
                    val printOutputThread = Thread.ofPlatform().start { () =>
                      outputLines.foreach(GitLogger.standard)
                    }
                    val printErrorsThread = Thread.ofPlatform().start { () =>
                      errorLines.foreach(GitLogger.standard)
                    }
                    val exitCode = process.waitFor()
                    printOutputThread.join()
                    printErrorsThread.join()
                    if (exitCode == 0)
                      SelfLogger.success(s"Cloned into $absPath")
                  }
                  catch {
                    case _: Throwable => SelfLogger.error("Clone failed")
                  }
                }
              case None =>
                SelfLogger.error(s"Bad url '$str'")
            }
          }
        }
      }
    )

class TerminalLogger(source: String) {
  def standard(message: String): Unit = Console.out.println(
    s"${inBracket(fansi.Color.DarkGray(source))} $message"
  )
  def warning(message: String): Unit = Console.out.println(
    s"${inBracket(fansi.Color.Yellow(source))} $message"
  )
  def success(message: String = ""): Unit = Console.out.println(
    inBracket(fansi.Color.Green(source)) + " " + message
  )
  def error(message: String): Unit = Console.err.println(
    inBracket(fansi.Color.Red(source)) + " " + message
  )
  private def inBracket(v: fansi.Str) = "[" + v + "]"
}

object SelfLogger extends TerminalLogger("src")
object GitLogger extends TerminalLogger("git")

object WithConfig {
  def apply(f: Config => Unit): Unit = {
    val config = Config.read().getOrElse {
      Config.write(Config.default)
      SelfLogger.warning("No config file found. Created default one")
      Config.default
    }
    f(config)
  }
}
