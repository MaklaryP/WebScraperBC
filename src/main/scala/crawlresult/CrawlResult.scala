package crawlresult

import utils.Url.Url
import utils.UrlVisitRecord

abstract class CrawlResult(urlVisitRecord: UrlVisitRecord) {
  def getStringToSave: String
}


object CrawlResult{
  def getLinksOnPage(results: Seq[CrawlResult]): Seq[Url] = {
    results.foldLeft(Seq.empty[Url]) {
      case (accSeq, Crawled(_, linksOnPage, _)) => accSeq ++ linksOnPage
      case (accSeq, _) => accSeq
    }
  }
}
