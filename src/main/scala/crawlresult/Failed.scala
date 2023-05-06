package crawlresult

import utils.UrlVisitRecord

case class Failed(urlVisitRecord: UrlVisitRecord, errorMsg: String) extends CrawlResult(urlVisitRecord) {
  override def getStringToSave: String = this.toString

  override protected def addToCrawledFailed(cf: (Int, Int)): (Int, Int) = (cf._1, cf._2 + 1)

}
