import utest._

object ConfigTests extends TestSuite {
  val tests = Tests {
    test("Config") {
      test("returns default workspace path if there is no config") {
        val actual = Config.workspacePath(os.temp.dir())
        val expected = Right(os.home/Config.defaultWorkspace)
        assert(actual == expected)
      }

      test("reads workspace path from existing config") {
        test("considers relative path to home") {
          val configBaseDir = os.temp.dir()
          os.write(configBaseDir/Config.configFileName, "test")
          val actual = Config.workspacePath(configBaseDir)
          val expected = Right(os.home/"test")
          assert(actual == expected)
        }

        test("treats abs path properly") {
          val configBaseDir = os.temp.dir()
          os.write(configBaseDir/Config.configFileName, "/abs/path")
          val actual = Config.workspacePath(configBaseDir)
          val expected = Right(os.root/"abs"/"path")
          assert(actual == expected)
        }
      }

      test("returns an error if the base path doesn't exists") {
        val pathDoesntExist = os.root/"doesntExist"
        val actual = Config.workspacePath(pathDoesntExist)
        val expected = Left(BasePathDoesntExist(pathDoesntExist))
        assert(actual == expected)
      }

      test("should take only first line as a workspace path") {
        val configBaseDir = os.temp.dir()
        os.write(configBaseDir/Config.configFileName, "/test1\n/test2\n")
        val actual = Config.workspacePath(configBaseDir)
        val expected = Right(os.root/"test1")
        assert(actual == expected)
      }

      test("return error if config is empty") {
        val configBaseDir = os.temp.dir()
        val configFile = configBaseDir/Config.configFileName
        os.write(configFile, Array[Byte]())
        val actual = Config.workspacePath(configBaseDir)
        val expected = Left(ConfigFileIsEmpty(configFile))
        assert(actual == expected)
      }
    }
  }
}
