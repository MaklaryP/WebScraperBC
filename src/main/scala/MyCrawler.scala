import domainscraper.DomainFilter
import logger.{LogContext, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import urlmanager.{UrlManager, VisitRecord, VisitState}
import urlmanager.VisitState.VisitState
import utils.CrawlerContext
import utils.CustomTypes.Url
import utils.Result._

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

  def crawlStep(toCrawl: Iterable[Url]): Seq[Result] = {
//    println(s"\n\n\nCrawled in this step: ${toCrawl.size}")
//    toCrawl.foreach(println)

    val grouped = toCrawl.grouped(chunkSize).toSeq.zipWithIndex
    val futures = grouped.map(i => crawlChunk(i._1, i._2.toString, grouped.size))
    val futureResult: Future[Seq[Result]] = Future.sequence(futures).map(_.flatten)

    val results: Seq[Result] = Await.result(futureResult, 5 minutes)

    println("Writing....")

    val writer = new BufferedWriter(new FileWriter(new File("C:\\Users\\peter.maklary\\Documents\\PARA-Work\\Projects\\WebScraperBC\\data\\fileDb.txt"), true))

    results.foreach(r => writer.write(r.getStringToSave + "\n"))
    writer.close()
    println("Finished Writing")

    //Save step resolt to the repo //todo save result to repo

    results
  }

  @tailrec
  final def crawlRec(qu: UrlManager, maxSteps: Int, logCtx: LogContext, results: Seq[Result]): Seq[Result] = {
    val crawlInThisStep = qu.toCrawlUrls(stepMaxSize)
//    println(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${restOfToCrawl.size}")
    val newLogCtx = crawlerCtx.logger.logWithContext(s"Crawl in step: ${crawlInThisStep.size} , Remaining: ${qu.size - crawlInThisStep.size}", logCtx, LogLevel.INFO)

    if (maxSteps == 0 || crawlInThisStep.isEmpty) results
    else {

      val stepResult: Seq[Result] = crawlStep(crawlInThisStep)
      val urlsFound: Iterable[Url] = getLinksOnPage(stepResult)

      def mapResToVisState(res: Result): VisitState = res match {
        case Crawled(_,_,_,_) =>  VisitState.Success
        case _ => VisitState.Fail
      }

      val now = Some(LocalDateTime.now())
      val crawledVisited = stepResult.map(result =>
        VisitRecord(result.url, now, mapResToVisState(result))
      )

      val toUpsert: Seq[VisitRecord] = crawledVisited ++ urlsFound.map(VisitRecord.unvisited)
      val newQu = qu.upsert(toUpsert)

      crawlRec(newQu, maxSteps - 1, newLogCtx, results ++ stepResult)
    }
  }

  def crawlMainJob(seedUrls: Seq[Url], maxSteps: Int = 2): Seq[Result] = {
    crawlRec(crawlerCtx.urlQueue.upsert(seedUrls.map(VisitRecord.unvisited)), maxSteps, LogContext(), Seq.empty)
  }


  private def crawlChunk(toCrawl: Iterable[Url], chunkId: String, numOfChunks: Int, dryRun: Boolean = false): Future[Iterable[Result]] = Future{
    val allCount = toCrawl.size
    toCrawl.zipWithIndex.map{case(url, index) => {
//      println(s"Chunk[$chunkId/$numOfChunks]: $index out of $allCount --> $url")
      crawlUrl(url)
    }}
  }

  private def crawlUrl(url: String): Result = {
    try {
      //todo filter links to point only to articles

      val browser = JsoupBrowser()
      //todo add browser and other singletons to crawlerContext

      val parser = DomainFilter.getDomainScraper(url).getOrElse(throw new RuntimeException(s"Unknown domain for url: $url"))
      val (cont, urls) = parser.parseDocument(browser.get(url))
      val supportedUrls = urls.filter(DomainFilter.isUrlInSupportedDomains)

      Crawled(url, url, supportedUrls, cont)
    } catch {
      case e: Exception =>
        Failed(url, url, e.getMessage)
    }
  }


}
