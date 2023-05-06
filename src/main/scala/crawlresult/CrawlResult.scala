package crawlresult

import utils.Url.Url
import utils.{RunStats, UrlVisitRecord}

abstract class CrawlResult(urlVisitRecord: UrlVisitRecord) {
  def getStringToSave: String

  protected def addToCrawledFailed(cf: (Int, Int)): (Int, Int)
}


object CrawlResult{
  def aggregateStats(stepResult: Seq[CrawlResult], runStats: RunStats): RunStats = {

    val (c,f) = stepResult.foldLeft((0,0)){case (acc, cRes) => cRes.addToCrawledFailed(acc)}

    runStats.copy(
      numOfCrawled = runStats.numOfCrawled + c,
      numOfFailed = runStats.numOfFailed + f
    )
  }

  def getLinksOnPage(results: Seq[CrawlResult]): Seq[Url] = {
    results.foldLeft(Seq.empty[Url]) {
      case (accSeq, Crawled(_, linksOnPage, _)) => accSeq ++ linksOnPage
      case (accSeq, _) => accSeq
    }
  }


}
