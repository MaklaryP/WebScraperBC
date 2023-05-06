import crawlresult.{CrawlResult, Crawled, Failed}
import domainscraper.DomainFilter
import logger.{LogContext, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import repository.Repository
import urlmanager.UrlManager
import utils.Url.Url
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport, UrlVisitRecord}

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

  def crawlStep(toCrawl: Iterable[Url]): Seq[CrawlResult] = {
    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex
    val futures = grouped.map(i => crawlChunk(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[CrawlResult]] = Future.sequence(futures).map(_.flatten)

    val results: Seq[CrawlResult] = Await.result(futureResult, 5 minutes)

    results
  }

  @tailrec
  final def doStep(qu: UrlManager, repo: Repository, logCtx: LogContext, numOfSteps: BigInt, crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    val crawlInThisStep = qu.getBatch(stepMaxSize)
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")
    val newLogCtx = crawlerCtx.logger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${qu.sizeToCrawl - crawlInThisStep.size}", logCtx, LogLevel.INFO)

    if(crawlInThisStep.isEmpty) CrawlerRunReport(numOfSteps, "All URLs crawled.")
    else if(crawlLimit.shouldStopCrawling(numOfSteps)) CrawlerRunReport(numOfSteps, "Crawl stopped due to limit")
    else {

      val stepResult: Seq[CrawlResult] = crawlStep(crawlInThisStep)
      val urlsFound: Iterable[Url] = CrawlResult.getLinksOnPage(stepResult)

      val newQu = qu.upsert(urlsFound.toSeq)

      repo.saveStep(stepResult)

      doStep(newQu, repo, newLogCtx, numOfSteps + 1, crawlLimit)
    }
  }

  def crawlMainJob(seedUrls: Seq[Url], crawlLimit: CrawlLimit.CrawlLimit): CrawlerRunReport = {
    doStep(crawlerCtx.urlQueue.upsert(seedUrls), crawlerCtx.repo, LogContext(), 1, crawlLimit)
  }


  private def crawlChunk(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int, dryRun: Boolean = false): Future[Iterable[CrawlResult]] = Future{
    val allCount = toCrawl.size
    toCrawl.zipWithIndex.map{case(url, index) =>
      //      println(s"Chunk[$chunkId/$numOfChunks]: $index out of $allCount --> $url")
      crawlUrl(url)
    }
  }

  private def crawlUrl(url: Url): CrawlResult = {
    try {
      //todo filter links to point only to articles

      val browser = JsoupBrowser()
      //todo add browser and other singletons to crawlerContext

      val parser = DomainFilter.getDomainScraper(url).getOrElse(throw new RuntimeException(s"Unknown domain for url: $url"))
      val (cont, urls) = parser.parseDocument(browser.get(url)) //todo change how we are visiting and geting report of it
      val visitRecord = UrlVisitRecord(url, LocalDateTime.now())

      val supportedUrls = urls.filter(DomainFilter.isUrlInSupportedDomains)

      Crawled(visitRecord, supportedUrls, cont)
    } catch {
      case e: Exception =>
        Failed(UrlVisitRecord(url, LocalDateTime.now()), e.getMessage)
    }
  }


}
