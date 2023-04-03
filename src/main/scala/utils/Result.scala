package utils

import net.ruippeixotog.scalascraper.scraper.HtmlExtractor

object Result {

  type Url = String
  case class PageContent(title: String, authors: Seq[String],
                         publishDate: String, lastUpdatedDate: String,
                         articleText: String, tags: Seq[String],
                         relatedArticles: Seq[Url], sourceArticles: Seq[Url])

  object PageContent {
    def getEmpty: PageContent = {
      PageContent(title = "", authors = Seq.empty,
        publishDate = "", lastUpdatedDate = "",
        articleText = "", tags = Seq.empty,
        relatedArticles = Seq.empty, sourceArticles = Seq.empty)
    }

    def getFromOptions(title: Option[String], authors: Seq[String],
                          publishDate: Option[String], lastUpdatedDate: Option[String],
                          articleText: Option[String], tags: Seq[String],
                          relatedArticles: Seq[Url], sourceArticles: Seq[Url]): PageContent = {
      PageContent(title.getOrElse(""), authors,
        publishDate.getOrElse(""), lastUpdatedDate.getOrElse(""),
        articleText.getOrElse(""), tags,
        relatedArticles, sourceArticles)
    }
  }

//  case class PageId(id: String)
  //todo how to check pageId uniqueness - use enum

  trait Result {
    def getStringToSave: String
  }

  case class Crawled(pageId: String, url: Url, linksOnPage: Seq[Url], pageContent: PageContent) extends Result {
    override def getStringToSave(): String = s"Crawled(${pageContent.toString})"
  }

  case class Failed(pageId: String, url: Url, failReason: String) extends Result {
    override def getStringToSave(): String = this.toString
  }

  def getLinksOnPage(results: Seq[Result]): Seq[Url] = {
    results.foldLeft(Seq.empty[Url]){
      case (accSeq, Crawled(_, _, linksOnPage, _)) => accSeq ++ linksOnPage
      case (accSeq, _) => accSeq
    }
  }

}
