package crawler

import dto.{PageContent, RepoDTO, UrlVisitRecord}
import dto.Url.Url
import dto.crawlresult.{CrawlResult, Crawled, Failed}
import org.scalatest.matchers.should.Matchers._
import repository.InMemoryRepo
import urlmanager.InMemoryUM
import utils.{CrawlLimit, CrawlerContext, RunStats}

class MainTestScenarios extends org.scalatest.funsuite.AnyFunSuite{

  test("Scenerio: Crawled all urls"){

    val testPageContent = PageContent(
      Some("SomeTitle"),
      Seq("authorA", "authorB"),
      Some("SomeIntroSection"),
      Some("SomeArticleText"),
      Some("2022-05-20"),
      Some("2022-05-22"),
      Seq("aabb", "aacc")
    )

    val testPageContent2 = testPageContent
    val crawledA = Crawled(UrlVisitRecord("a"), Seq("aa", "ab", "same"), testPageContent)
    val crawledB = Crawled(UrlVisitRecord("b"), Seq("ba", "same", "x"),
      testPageContent2.copy(articleText = Some("TitleB")))

    val crawledAA = Crawled(UrlVisitRecord("aa"), Seq(), testPageContent)
    val crawledAB = Crawled(UrlVisitRecord("ab"), Seq(), testPageContent)
    val crawledSame = Crawled(UrlVisitRecord("same"), Seq(), testPageContent)
    val crawledBA = Crawled(UrlVisitRecord("ba"), Seq(), testPageContent)
    val failedX = Failed(UrlVisitRecord("x"), "Some Error Msg")


    //Check that each url was visited exactly once
    val visitingRecord: scala.collection.mutable.Map[Url, Int] = scala.collection.mutable.Map.empty

    val repo = new InMemoryRepo()

    val ctx = CrawlerContext(
      (u: Url) => {
        visitingRecord.update(u, visitingRecord.getOrElse(u, 0) + 1)

        u match {
          case "a" => CrawlResult().addCrawled(crawledA)
          case "aa" => CrawlResult().addCrawled(crawledAA)
          case "ab" => CrawlResult().addCrawled(crawledAB)
          case "b" => CrawlResult().addCrawled(crawledB)
          case "ba" => CrawlResult().addCrawled(crawledBA)
          case "same" => CrawlResult().addCrawled(crawledSame)
          case "x" => CrawlResult().addFailed(failedX)
        }
      },
      new InMemoryUM(),
      repo
    )

    val crawler = new MyCrawler(2)
    val r = crawler.crawlMainJob(ctx, Seq("a", "b"), CrawlLimit.StepLimitedCrawl(30))

    r.runStats.numOfCrawled shouldBe 6
    r.runStats.numOfFailed shouldBe 1
    r.numberOfStepsCrawled shouldBe 5 //one more to get empty step

    //Each url should be visited exactly once
    visitingRecord.forall(p => p._2 == 1) shouldBe true
    visitingRecord.size shouldBe 7

    val expectedRepoDtos: Seq[RepoDTO] =
      Seq(
        crawledA,
        crawledAA,
        crawledAB,
        crawledB,
        crawledBA,
        crawledSame
      ).map(mappers.mappers.crawledToRepoDTO) :+ mappers.mappers.failedToRepoDTO(failedX)

    repo.getAllSaved() should contain theSameElementsAs expectedRepoDtos

  }

}
