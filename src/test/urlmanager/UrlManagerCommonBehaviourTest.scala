package urlmanager

import org.scalatest.matchers.should.Matchers._
import utils.Url.Url
import utils.UrlVisitRecord

import java.time.LocalDateTime

abstract class UrlManagerCommonBehaviourTest extends org.scalatest.funsuite.AnyFunSuite {

  protected def getUmImpl(toCrawl: Seq[Url], alreadyVisited: Set[Url]): UrlManager
  protected def getUmImpl(): UrlManager


  test("Creating with dupl urls should deduplicate") {
    val um = getUmImpl(Seq("x", "x"), Set.empty)

    um.sizeToCrawl shouldBe 1
    um.getBatch(100) shouldBe Seq("x")
  }

  test("Creating with already visited should get them to crawl") {
    val um = getUmImpl(Seq("x", "y"), Set("x"))

    um.getBatch(100) shouldBe Seq("y")
  }


  test("Attempting to upsert same URLs should upsert only one"){
    val um = getUmImpl().upsert(Seq("x", "x"))

    um.sizeToCrawl shouldBe 1
    um.getBatch(100) shouldBe Seq("x")
  }

  test("Consecutive exhaustive batch should get same elements") {
    val um = getUmImpl().upsert(Seq("x", "y", "z"))
    val um2 = um.getBatch(100)

    um.getBatch(100) should contain theSameElementsAs  um2
  }

  test("Getting batch should get number of elements as batchSize") {
    val um = getUmImpl().upsert(Seq("x", "y", "z"))

    um.getBatch(2) should have size 2
  }

  test("Should not batch urls marked as crawled") {
    val now = LocalDateTime.now()
    val um = getUmImpl()
      .upsert(Seq("x", "y", "z"))
      .markAsCrawled(Seq("x", "z").map(UrlVisitRecord(_, now)))

    um.getBatch(100) shouldBe Seq("y")
  }

  test("Consecutive marking should mark urls in both sets to mark") {
    val now = LocalDateTime.now()
    val um = getUmImpl()
      .upsert(Seq("a", "b", "c", "d", "e"))
      .markAsCrawled(Seq("a", "c").map(UrlVisitRecord(_, now)))
      .markAsCrawled(Seq("e").map(UrlVisitRecord(_, now)))

    um.getBatch(100) should contain theSameElementsAs Seq("b", "d")
  }

  test("Mark as crawled urls that are not in toCrawl should markThem after upserting"){
    val now = LocalDateTime.now()
    val um = getUmImpl()
      .markAsCrawled(Seq("x", "z").map(UrlVisitRecord(_, now)))
      .upsert(Seq("x", "y", "z"))

    um.getBatch(100) shouldBe Seq("y")
  }
}
