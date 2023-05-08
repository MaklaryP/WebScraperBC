package urlmanager
import dto.Url.Url
import dto.UrlVisitRecord
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers._

import java.io.File
import java.nio.file.Path

class PersistedInMemoryUMTest extends urlmanager.UrlManagerCommonBehaviourTest with BeforeAndAfterEach with BeforeAndAfterAll{

  private val serPath = Path.of("testUm.ser").toAbsolutePath

  override protected def afterEach(): Unit = {
    val f = serPath.toFile
    if(f.exists()) f.delete()
  }


  override protected def afterAll(): Unit = {
    val f = serPath.toFile
    if (f.exists()) f.delete()
  }

  override protected def getUmImpl(toCrawl: Seq[Url], alreadyVisited: Set[Url]): UrlManager = new PersistedInMemoryUM(serPath, InMemoryUM.create(toCrawl, alreadyVisited))

  override protected def getUmImpl(): UrlManager = new PersistedInMemoryUM(serPath)


  test("Creating new should not load from file"){
    val umA = getUmImpl(Seq("a", "b"), Set.empty)
    umA.upsert(Seq("c", "d"))

    val umB = getUmImpl(Seq("x", "y"), Set.empty)

    umB.getBatch(100) should contain theSameElementsAs Seq("x", "y")
  }

  test("Upsert should be persisted"){
    val umA = getUmImpl(Seq("a", "b"), Set.empty)
    umA.upsert(Seq("c", "d"))

    val umB = new PersistedInMemoryUM(serPath)
    umB.loadFromFile()

    umB.getBatch(100) should contain theSameElementsAs "abcd".toSeq.map(_.toString)
  }

  test("Mark as crawled should be persisted") {
    val umA = getUmImpl(Seq("a", "b", "c"), Set.empty)
    umA.markAsCrawled(Seq("b", "c").map(UrlVisitRecord(_)))

    val umB = new PersistedInMemoryUM(serPath)
    umB.loadFromFile()

    umB.getBatch(100) should contain theSameElementsAs Seq("a")
  }


}
