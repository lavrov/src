import utest._

object GitTests extends TestSuite {
  val tests = Tests {
    test("Url") {
      test("parses http url") {
        val actual = Git.Url.parse("http://github.com/lavrov/src.git")
        val expected = Some(Git.Url("github.com", "lavrov/src.git"))
        assert(actual == expected)
      }
      test("parses ssh url") {
        val actual = Git.Url.parse("git@github.com:lavrov/src.git")
        val expected = Some(Git.Url("github.com", "lavrov/src.git"))
        assert(actual == expected)
      }
    }
  }
}
