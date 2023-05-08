import domainscraper.DomainFilter
import dto.UrlVisitRecord
import dto.crawlresult.{CrawlResult, Crawled, Failed}
import logger.{CLogger, LogContext, LogLevel}
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



sealed class MyCrawler {

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

  def doStep(ctx: CrawlerContext): (CrawlerContext, RunStats) = {
    val crawlInThisStep = ctx.urlQueue.getBatch(stepMaxSize)
    val newLogCtx = CLogger.getLogger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${ctx.urlQueue.sizeToCrawl - crawlInThisStep.size}", ctx.logCtx, LogLevel.INFO)

    val stepResult: CrawlResult = crawlStep(crawlInThisStep)
    val urlsFound: Iterable[Url] = stepResult.crawled.map(_.linksOnPage).reduce(_ ++ _)

//    val newStats = runStats ++ RunStats(stepResult.crawled.size, stepResult.failed.size)
    val runStats = RunStats(stepResult.crawled.size, stepResult.failed.size)

    val newQu = ctx.urlQueue.upsert(urlsFound.toSeq)


    ctx.repo.saveStep(
      stepResult.crawled.map(mappers.mappers.crawledToRepoDTO) ++
        stepResult.failed.map(mappers.mappers.failedToRepoDTO)
    )

    //todo mark as crawled

    (ctx.copy(urlQueue = newQu).copy(logCtx = newLogCtx), runStats)
  }

  @tailrec
  final def stepController(ctx: CrawlerContext, numOfSteps: BigInt, crawlLimit: CrawlLimit.CrawlLimit, runStats: RunStats): CrawlerRunReport = {
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")

    val (newCtx, stepRunStats) = doStep(ctx)

    if(runStats.nothingNewCrawled(stepRunStats)) CrawlerRunReport(runStats, numOfSteps, "All URLs crawled.")
    else if(crawlLimit.shouldStopCrawling(numOfSteps)) CrawlerRunReport(runStats, numOfSteps, s"Crawl stopped due to limit: ${crawlLimit.getClass}")
    else stepController(ctx, numOfSteps + 1, crawlLimit, runStats ++ stepRunStats)
  }

  def crawlMainJob(ctx: CrawlerContext, seedUrls: Seq[Url], crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    val ctxWithSeeds = ctx.copy(urlQueue = ctx.urlQueue.upsert(seedUrls))
    stepController(ctxWithSeeds, 1, crawlLimit, RunStats(0, 0))
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
