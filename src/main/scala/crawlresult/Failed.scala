package crawlresult

import utils.UrlVisitRecord

case class Failed(urlVisitRecord: UrlVisitRecord, errorMsg: String) extends CrawlResult(urlVisitRecord) {
  override def getStringToSave: String = this.toString
}
