import cats.syntax.all._

object Git {

  case class Url(
    server: String,
    path: String,
  )

  object Url {

    private val sshVariant = """^(git@)?([\w.-]+):([\w\/.-]+)$""".r
    private val httpVariant = """^https?:\/\/([\w.-]+)/([\w\/.-]+)$""".r

    def parse(s: String): Option[Url] = s match {
      case httpVariant(server, path) =>
        Url(server, path).some
      case sshVariant(_, server, path) =>
        Url(server, path).some
      case _ =>
        none
    }
  }


}