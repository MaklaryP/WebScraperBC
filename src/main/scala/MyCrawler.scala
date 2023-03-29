import utils.Result._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import collection.JavaConverters._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

object MyCrawler {

  implicit val ec = ExecutionContext.global
  /*
  Worker gets bunch of URLs
   */

  def crawl(seedUrls: Seq[Url], maxRecLvl: Int = 2, dryRun: Boolean = false): (Set[Url], Seq[Result]) = {
    def crawlRec(toCrawl: Set[Url], maxRecLvl: Int, visited: Set[Url], results: Seq[Result]): (Set[Url], Seq[Result]) = {
      if(maxRecLvl == 0) (visited, results)
      else {
        val grouped = toCrawl.toSeq.grouped(10).toSeq.zipWithIndex
        val futures = grouped.map(i => crawlChunk(i._1, i._2.toString, grouped.size))
        val futureResult: Future[Seq[Result]] = Future.sequence(futures).map(_.flatten)

        val crawled: Seq[Result] = Await.result(futureResult, 5 minutes)


        val newVisited = visited ++ toCrawl
        val nextToCrawl = getLinksOnPage(crawled).toSet -- newVisited

        println(s"\n\n\nCrawled in next step: ${nextToCrawl.size}")
//        nextToCrawl.foreach(println)

        crawlRec(nextToCrawl, maxRecLvl - 1, newVisited, results ++ crawled)
      }
    }

    crawlRec(seedUrls.toSet, maxRecLvl, Set.empty, Seq.empty)
  }


  private def crawlChunk(toCrawl: Seq[Url], chunkId: String, numOfChunks: Int, dryRun: Boolean = false): Future[Seq[Result]] = Future{
    val allCount = toCrawl.size
    toCrawl.zipWithIndex.map{case(url, index) => {
      println(s"Chunk[$chunkId/$numOfChunks]: $index out of $allCount --> $url")
      crawlUrl(url, dryRun)
    }}
  }

  private def crawlUrl(url: String, dryRun: Boolean = false): Result = {
    if(dryRun) Failed(url, url, "Dry run")
    else try {
      val doc: Document = Jsoup.connect(url).get()
      val links = doc.select("a[href]").iterator.asScala.toList.map(_.attr("abs:href"))
      Crawled(url, url, links, PageContent())
    } catch {
      case e: Exception =>
        Failed(url, url, e.getMessage)
    }
  }


}
