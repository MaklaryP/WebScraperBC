package crawler

import domainscraper.DomainFilter
import dto.Url.Url
import dto.UrlVisitRecord
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



class MyCrawler(stepMaxSize: Int = 1000, chunkSize: Int = 50) {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  /*
  Worker gets bunch of URLs
   */

  def crawlStep(crawlUrlFun: Url => CrawlResult)(toCrawl: Iterable[Url]): CrawlResult = {
    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex.map(p => (p._1, p._2 + 1)) //we want chunkId to start from 1
    val futures = grouped.map(i => crawlChunk(crawlUrlFun)(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[CrawlResult]] = Future.sequence(futures)

    Await.result(futureResult, 5 minutes).reduceOption(_ ++ _).getOrElse(CrawlResult())
  }

  def doStep(ctx: CrawlerContext): (CrawlerContext, RunStats) = {
    val crawlInThisStep = ctx.urlManager.getBatch(stepMaxSize)
    val newLogCtx = CLogger.getLogger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${ctx.urlManager.sizeToCrawl - crawlInThisStep.size}", ctx.logCtx, LogLevel.INFO)

    val stepResult: CrawlResult = crawlStep(ctx.crawlUrlFun)(crawlInThisStep)
    val urlsFound: Iterable[Url] = stepResult.crawled.map(_.supportedUrlsOnPage).reduceOption(_ ++ _).getOrElse(Iterable.empty)

//    val newStats = runStats ++ RunStats(stepResult.crawled.size, stepResult.failed.size)
    val runStats = RunStats(stepResult.crawled.size, stepResult.failed.size)

    ctx.urlManager.upsert(urlsFound.toSeq)


    ctx.repo.saveStep(
      stepResult.crawled.map(mappers.mappers.crawledToRepoDTO) ++
        stepResult.failed.map(mappers.mappers.failedToRepoDTO)
    )


    ctx.urlManager.markAsCrawled(crawlInThisStep.map(UrlVisitRecord(_)))

    (ctx.copy(logCtx = newLogCtx), runStats)
  }

  @tailrec
  final def stepController(ctx: CrawlerContext, numOfSteps: BigInt, crawlLimit: CrawlLimit.CrawlLimit, runStats: RunStats): CrawlerRunReport = {
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")

    val (newCtx, stepRunStats) = doStep(ctx)

    if(stepRunStats.doneNothing()) CrawlerRunReport(runStats, numOfSteps, "All URLs crawled.")
    else if(crawlLimit.shouldStopCrawling(numOfSteps)) CrawlerRunReport(runStats, numOfSteps, s"Crawl stopped due to limit: ${crawlLimit.getClass}")
    else stepController(newCtx, numOfSteps + 1, crawlLimit, runStats ++ stepRunStats)
  }

  def crawlMainJob(ctx: CrawlerContext, seedUrls: Seq[Url], crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    ctx.urlManager.upsert(seedUrls)
    val ctxWithSeeds = ctx
    stepController(ctxWithSeeds, 1, crawlLimit, RunStats(0, 0))
  }


  private def crawlChunk(crawlUrlFun: Url => CrawlResult)(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int): Future[CrawlResult] = {
    CLogger.getLogger.log(s"Crawling chunk [$chunkId/$numOfChunks]", LogLevel.DEBUG)
    Future{
      toCrawl.map(crawlUrlFun).reduceOption(_ ++ _).getOrElse(CrawlResult())
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

      val parser = DomainFilter.getDomainScraper(url).getOrElse(throw new RuntimeException(s"Not supported domain - url: $url"))
      val cont = parser.parseDocument(visitUrlFun(url))
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
