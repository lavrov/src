import os.{Path, FilePath, RelPath}

object Config {
  lazy val defaultWorkspace = "workspace"
  lazy val configFileName = ".srcrc"

  type ConfigResult = Either[ConfigError, Path]

  def workspacePath: ConfigResult = workspacePath(os.home)

  // TODO: get rid of side-effects
  def workspacePath(baseDir: Path): ConfigResult = {
    if (os exists baseDir) {
      val configFile = baseDir/configFileName

      if (os.exists(configFile)) {
        // TODO: extract parser
        os.read.lines(configFile).toList.headOption.map(FilePath(_)) match {
          case Some(r: RelPath) => Right(Path(r.toString, os.home))
          case Some(p: Path) => Right(p)
          case None => Left(ConfigFileIsEmpty(configFile))
        }
      } else {
        val workspacePath = os.home/defaultWorkspace

        os.write(configFile, workspacePath.toString)
        TerminalUtil.warning(s"No config file found at $configFile. Created one with default workspace path: $workspacePath")

        Right(workspacePath)
      }
    } else {
      Left(BasePathDoesntExist(baseDir))
    }
  }
}

sealed trait ConfigError
final case class BasePathDoesntExist(path: FilePath) extends ConfigError
final case class ConfigFileIsEmpty(configFile: FilePath) extends ConfigError
