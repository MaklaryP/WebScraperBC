package urlmanager

import dto.UrlVisitRecord
import org.scalatest.matchers.should.Matchers._
import dto.Url.Url

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
    val um = getUmImpl()
    um.upsert(Seq("x", "x"))

    um.sizeToCrawl shouldBe 1
    um.getBatch(100) shouldBe Seq("x")
  }

  test("Consecutive exhaustive batch should get same elements") {
    val um = getUmImpl()
    um.upsert(Seq("x", "y", "z"))
    val batch1 = um.getBatch(100)

    um.getBatch(100) should contain theSameElementsAs  batch1
  }

  test("Getting batch should get number of elements as batchSize") {
    val um = getUmImpl()
    um.upsert(Seq("x", "y", "z"))

    um.getBatch(2) should have size 2
  }

  test("Should not batch urls marked as crawled") {
    val now = LocalDateTime.now()
    val um = getUmImpl()
    um.upsert(Seq("x", "y", "z"))
    um.markAsCrawled(Seq("x", "z").map(dto.UrlVisitRecord(_, now)))

    um.getBatch(100) shouldBe Seq("y")
  }

  test("Consecutive marking should mark urls in both sets to mark") {
    val now = LocalDateTime.now()
    val um = getUmImpl()
    um.upsert(Seq("a", "b", "c", "d", "e"))
    um.markAsCrawled(Seq("a", "c").map(dto.UrlVisitRecord(_, now)))
    um.markAsCrawled(Seq("e").map(dto.UrlVisitRecord(_, now)))

    um.getBatch(100) should contain theSameElementsAs Seq("b", "d")
  }

  test("Mark as crawled urls that are not in toCrawl should markThem after upserting"){
    val now = LocalDateTime.now()
    val um = getUmImpl()
    um.markAsCrawled(Seq("x", "z").map(dto.UrlVisitRecord(_, now)))
    um.upsert(Seq("x", "y", "z"))

    um.getBatch(100) shouldBe Seq("y")
  }
}
