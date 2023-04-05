package utils

import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import utils.CustomTypes.Url

object Result {

//  case class PageId(id: String)
  //todo how to check pageId uniqueness - use enum

  trait Result {
    def getStringToSave: String
  }

  case class Crawled(pageId: String, url: Url, linksOnPage: Seq[Url], pageContent: PageContent) extends Result {
    override def getStringToSave: String = s"Crawled(${pageContent.toString})"
  }

  case class Failed(pageId: String, url: Url, failReason: String) extends Result {
    override def getStringToSave: String = this.toString
  }

  def getLinksOnPage(results: Seq[Result]): Seq[Url] = {
    results.foldLeft(Seq.empty[Url]){
      case (accSeq, Crawled(_, _, linksOnPage, _)) => accSeq ++ linksOnPage
      case (accSeq, _) => accSeq
    }
  }

}
