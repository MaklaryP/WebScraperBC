package crawlresult

import utils.Url.Url
import utils.{PageContent, UrlVisitRecord}

case class Crawled(urlVisitRecord: UrlVisitRecord, linksOnPage: Seq[Url], pageContent: PageContent) extends CrawlResult(urlVisitRecord) {
  override def getStringToSave: Url = s"Crawled(${urlVisitRecord.url})"

  override protected def addToCrawledFailed(cf: (Int, Int)): (Int, Int) = (cf._1 + 1, cf._2)

}

