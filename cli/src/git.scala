object Git {

  case class Url(
    url: String,
    server: String,
    path: String,
  )

  object Url {

    private val sshVariant = """^(git@)?([\w.-]+):([\w\/.-]+)$""".r
    private val httpVariant = """^https?:\/\/([\w.-]+)/([\w\/.-]+)$""".r
    private val shortVariant = """^([\w.-]+)/([\w\/.-]+)$""".r

    def parse(s: String): Option[Url] =
      PartialFunction.condOpt(s) {
        case httpVariant(server, path) =>
          Url(s, server, path)
        case sshVariant(_, server, path) =>
          Url(s, server, path)
        case shortVariant(user, repository) =>
          Url(s"git@github.com:$user/$repository.git", "github.com", s"$user/$repository.git")
      }
  }

}
