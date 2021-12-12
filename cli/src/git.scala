object Git {

  case class Url(
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
          Url(server, path)
        case sshVariant(_, server, path) =>
          Url(server, path)
        case shortVariant(user, repository) =>
          Url("github.com", s"$user/$repository.git")
      }
  }

}
