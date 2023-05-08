package crawler

import domainscraper.DomainFilter
import dto.Url.Url
import dto.crawlresult.{CrawlResult, Crawled, Failed}
import logger.{CLogger, LogLevel}
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.model.Document
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport, RunStats}

import java.time.LocalDateTime
import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.language.postfixOps



class MyCrawler {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  /*
  Worker gets bunch of URLs
   */

  // Step - each step is running crawl in chunks asynchronously, saving result in main thread
  val stepMaxSize = 1000
  val chunkSize = 50 // one worker load

  def crawlStep(crawlUrlFun: Url => CrawlResult)(toCrawl: Iterable[Url]): CrawlResult = {
    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex.map(p => (p._1, p._2 + 1)) //we want chunkId to start from 1
    val futures = grouped.map(i => crawlChunk(crawlUrlFun)(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[CrawlResult]] = Future.sequence(futures)

    Await.result(futureResult, 5 minutes).reduce(_ ++ _)
  }

  def doStep(ctx: CrawlerContext): (CrawlerContext, RunStats) = {
    val crawlInThisStep = ctx.urlQueue.getBatch(stepMaxSize)
    val newLogCtx = CLogger.getLogger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${ctx.urlQueue.sizeToCrawl - crawlInThisStep.size}", ctx.logCtx, LogLevel.INFO)

    val stepResult: CrawlResult = crawlStep(ctx.crawlUrlFun)(crawlInThisStep)
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
    else stepController(newCtx, numOfSteps + 1, crawlLimit, runStats ++ stepRunStats)
  }

  def crawlMainJob(ctx: CrawlerContext, seedUrls: Seq[Url], crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    val ctxWithSeeds = ctx.copy(urlQueue = ctx.urlQueue.upsert(seedUrls))
    stepController(ctxWithSeeds, 1, crawlLimit, RunStats(0, 0))
  }


  private def crawlChunk(crawlUrlFun: Url => CrawlResult)(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int): Future[CrawlResult] = {
    CLogger.getLogger.log(s"Crawling chunk [${chunkId}/${numOfChunks}]", LogLevel.DEBUG)
    Future{
      toCrawl.map(crawlUrlFun).reduce(_ ++ _)
    }
  }




}

object MyCrawler{

  def visitUrl(browser: Browser)(url: Url): Document = {
    browser.get(url)
  }

  def crawlUrl(visitUrlFun: Url => Document)(url: Url): CrawlResult = {
    try {
      //todo filter links to point only to articles
      //todo add browser and other singletons to crawlerContext

      val parser = DomainFilter.getDomainScraper(url).getOrElse(throw new RuntimeException(s"Unknown domain for url: $url"))
      val cont = parser.parseDocument(visitUrlFun(url)) //todo change how we are visiting and geting report of it
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
