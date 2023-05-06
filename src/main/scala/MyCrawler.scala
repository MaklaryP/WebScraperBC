import crawlresult.{CrawlResult, Crawled, Failed}
import domainscraper.DomainFilter
import logger.{LogContext, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import urlmanager.UrlManager
import utils.{CrawlerContext, UrlVisitRecord}
import utils.Url.Url

import java.io.{BufferedWriter, File, FileWriter}
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
//    println(s"\n\n\nCrawled in this step: ${toCrawl.size}")
//    toCrawl.foreach(println)

    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex
    val futures = grouped.map(i => crawlChunk(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[CrawlResult]] = Future.sequence(futures).map(_.flatten)

    val results: Seq[CrawlResult] = Await.result(futureResult, 5 minutes)

    println("Writing....")
//    val path = "C:\\Users\\peter.maklary\\Documents\\PARA-Work\\Projects\\WebScraperBC\\data\\fileDb.txt"
    val path = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data\\fileDb.txt"
    val writer = new BufferedWriter(new FileWriter(new File(path), true))

    results.foreach(r => writer.write(r.getStringToSave + "\n"))
    writer.close()
    println("Finished Writing")

    //Save step resolt to the repo //todo save result to repo

    results
  }

  @tailrec
  final def crawlRec(qu: UrlManager, maxSteps: Int, logCtx: LogContext, results: Seq[CrawlResult]): Seq[CrawlResult] = {
    val crawlInThisStep = qu.getBatch(stepMaxSize)
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")
    val newLogCtx = crawlerCtx.logger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${qu.sizeToCrawl - crawlInThisStep.size}", logCtx, LogLevel.INFO)

    if (maxSteps == 0 || crawlInThisStep.isEmpty) results
    else {

      val stepResult: Seq[CrawlResult] = crawlStep(crawlInThisStep)
      val urlsFound: Iterable[Url] = CrawlResult.getLinksOnPage(stepResult)


      val newQu = qu.upsert(urlsFound.toSeq)

      crawlRec(newQu, maxSteps - 1, newLogCtx, results ++ stepResult)
    }
  }

  def crawlMainJob(seedUrls: Seq[Url], maxSteps: Int = 2): Seq[CrawlResult] = {
    crawlRec(crawlerCtx.urlQueue.upsert(seedUrls), maxSteps, LogContext(), Seq.empty)
  }


  private def crawlChunk(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int, dryRun: Boolean = false): Future[Iterable[CrawlResult]] = Future{
    val allCount = toCrawl.size
    toCrawl.zipWithIndex.map{case(url, index) => {
//      println(s"Chunk[$chunkId/$numOfChunks]: $index out of $allCount --> $url")
      crawlUrl(url)
    }}
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
