package crawler

import dto.Url.Url
import dto.{PageContent, RepoDTO, UrlVisitRecord}
import dto.crawlresult.{CrawlResult, Crawled}
import org.scalatest.matchers.should.Matchers._
import repository.{InMemoryRepo, Repository}
import urlmanager.InMemoryUM
import utils.CrawlerContext

import java.lang
import java.time.LocalDateTime

class StepTest extends org.scalatest.funsuite.AnyFunSuite{



  test("Step should save to the repo"){

    val testPageContent = PageContent(
      Some("SomeTitle"),
      Seq("authorA", "authorB"),
      Some("SomeIntroSection"),
      Some("SomeArticleText"),
      Some("2022-05-20"),
      Some("2022-05-22"),
      Seq("aabb", "aacc")
    )
    val testPageContent2 = testPageContent.copy(articleText = Some("AnotherArticleText"))
    val crawled1 = Crawled(UrlVisitRecord("aaaa"), Seq("aabb", "aacc"), testPageContent)
    val crawled2 = Crawled(UrlVisitRecord("bbbb"), Seq("bbaa", "bbcc"), testPageContent2)

    val repo = new InMemoryRepo()


    val testContext = CrawlerContext(
      (u: Url) => u match {
        case "aaaa" => CrawlResult().addCrawled(crawled1)
        case "bbbb" => CrawlResult().addCrawled(crawled2)
      },
      InMemoryUM.create(Seq("aaaa", "bbbb"), Set.empty),
      repo
    )

    val c = new MyCrawler()
    c.doStep(testContext)

    val expected: Seq[RepoDTO] = Seq(crawled1.copy(pageContent = crawled1.pageContent.copy(title = Some("xxxxxxx"))), crawled2).map(mappers.mappers.crawledToRepoDTO)

    repo.getAllSaved() should contain theSameElementsAs expected
  }

  test("Step should mark crawled as visited"){
    val repo = new InMemoryRepo()

    val testContext = CrawlerContext(
      (u: Url) => CrawlResult(),
      InMemoryUM.create(Seq("aaaa", "bbbb"), Set.empty),
      repo
    )

    val c = new MyCrawler()
    val (newContext, _) = c.doStep(testContext)

    newContext.urlQueue.getBatch(1000) shouldBe Seq()
  }

  test("Step should mark as visited after saved to repo"){
    val repo = new Repository {
      override def saveStep(stepResults: Seq[RepoDTO]): Unit = throw new RuntimeException("For the horde")
    }

    val testContext = CrawlerContext(
      (u: Url) => CrawlResult(),
      InMemoryUM.create(Seq("aaaa", "bbbb"), Set.empty),
      repo
    )

    val c = new MyCrawler()
    an [lang.RuntimeException] should be thrownBy(c.doStep(testContext))

    testContext.urlQueue.getBatch(100) shouldBe Seq("aaaa", "bbbb")

  }

}
