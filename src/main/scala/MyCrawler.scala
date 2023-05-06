import domainscraper.DomainFilter
import dto.UrlVisitRecord
import dto.crawlresult.{CrawlResult, Crawled, Failed}
import logger.{LogContext, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import repository.Repository
import urlmanager.UrlManager
import dto.Url.Url
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport, RunStats}

import java.time.LocalDateTime
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps



sealed class MyCrawler(crawlerCtx: CrawlerContext) {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  /*
  Worker gets bunch of URLs
   */

  // Step - each step is running crawl in chunks asynchronously, saving result in main thread
  val stepMaxSize = 1000
  val chunkSize = 50 // one worker load

  def crawlStep(toCrawl: Iterable[Url]): CrawlResult = {
    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex
    val futures = grouped.map(i => crawlChunk(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[CrawlResult]] = Future.sequence(futures)

    //todo try lowering limit
    Await.result(futureResult, 5 minutes).reduce(_ ++ _)
  }

  @tailrec
  final def doStep(qu: UrlManager, repo: Repository, logCtx: LogContext, numOfSteps: BigInt, crawlLimit: CrawlLimit.CrawlLimit, runStats: RunStats): CrawlerRunReport = {
    val crawlInThisStep = qu.getBatch(stepMaxSize)
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")

    if(crawlInThisStep.isEmpty) CrawlerRunReport(runStats, numOfSteps, "All URLs crawled.")
    else if(crawlLimit.shouldStopCrawling(numOfSteps)) CrawlerRunReport(runStats, numOfSteps, s"Crawl stopped due to limit: ${crawlLimit.getClass}")
    else {
      val newLogCtx = crawlerCtx.logger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${qu.sizeToCrawl - crawlInThisStep.size}", logCtx, LogLevel.INFO)

      val stepResult: CrawlResult = crawlStep(crawlInThisStep)
      val urlsFound: Iterable[Url] = stepResult.crawled.map(_.linksOnPage).reduce(_ ++ _)

      val newStats = runStats ++ RunStats(stepResult.crawled.size, stepResult.failed.size)

      val newQu = qu.upsert(urlsFound.toSeq)



      repo.saveStep(
        stepResult.crawled.map(mappers.mappers.crawledToRepoDTO) ++
          stepResult.failed.map(mappers.mappers.failedToRepoDTO)
        )

      //todo mark as crawled

      doStep(newQu, repo, newLogCtx, numOfSteps + 1, crawlLimit, newStats)
    }
  }

  def crawlMainJob(seedUrls: Seq[Url], crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    doStep(crawlerCtx.urlQueue.upsert(seedUrls), crawlerCtx.repo, LogContext(), 1, crawlLimit, RunStats(0, 0))
  }


  private def crawlChunk(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int, dryRun: Boolean = false): Future[CrawlResult] = {
    Future{
      toCrawl.map(crawlUrl).reduce(_ ++ _)
    }
  }

  private def crawlUrl(url: Url): CrawlResult = {
    try {
      //todo filter links to point only to articles

      val browser = JsoupBrowser()
      //todo add browser and other singletons to crawlerContext

      val parser = DomainFilter.getDomainScraper(url).getOrElse(throw new RuntimeException(s"Unknown domain for url: $url"))
      val cont = parser.parseDocument(browser.get(url)) //todo change how we are visiting and geting report of it
      val urls = cont.childArticles
      val visitRecord = dto.UrlVisitRecord(url, LocalDateTime.now())

      val supportedUrls = urls.filter(DomainFilter.isUrlInSupportedDomains)

      CrawlResult().addCrawled(Crawled(visitRecord, supportedUrls, cont))
    } catch {
      case e: Exception =>
        CrawlResult().addFailed(Failed(dto.UrlVisitRecord(url, LocalDateTime.now()), e.getMessage))
    }
  }


}
