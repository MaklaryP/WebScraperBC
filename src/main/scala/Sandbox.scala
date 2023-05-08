import urlmanager._
import utils.PathsSetting

import java.nio.file.Path

object Sandbox extends App {

//  val paths = PathsSetting.getPaths
//
//  val x = new InMemoryUM()
//  val um = new PersistedInMemoryUM(x, paths.umSer)
//  um.loadFromFile()

  println(Path.of("testUm.ser").toAbsolutePath)

}
