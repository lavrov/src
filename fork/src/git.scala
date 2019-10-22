import cats.syntax.all._

object Git {

  case class Url(
    server: String,
    path: String,
  )

  object Url {

    private val regex = """^(git@)?([\w.-]+):([\w\/.-]+)$""".r

    def parse(s: String): Option[Url] = s match {
      case regex(_, server, path) =>
        Url(server, path).some
      case _ =>
        none
    }
  }


}