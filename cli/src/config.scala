import os.{Path, FilePath, RelPath}

case class Config(
  workspace: String
)
object Config {
  val default = Config(
    workspace = "~/workspace"
  )
  private val configFilePath = os.home / ".config" / "src" / "config"
  private implicit val configRW = upickle.default.macroRW[Config]

  def read(): Option[Config] = {
    if (os exists configFilePath) {
      val configString = os.read(configFilePath)
      val config = upickle.default.read[Config](configString)
      Some(config)
    }
    else None
  }

  def write(config: Config): Unit = {
    val configString = upickle.default.write(config, indent = 4)
    os.write(configFilePath, configString, createFolders = true)
  }
}