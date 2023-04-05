package utils

import utils.CustomTypes._

case class PageContent(title: Option[String], authors: Seq[String],
                       publishDate: Option[String], lastUpdatedDate: Option[String],
                       articleText: Option[String], tags: Seq[Tag], categories: Seq[Category],
                       relatedArticles: Seq[Url], sourceArticles: Seq[Url])

object PageContent {
  def getEmpty: PageContent = {
    PageContent(title = None, authors = Seq.empty,
      publishDate = None, lastUpdatedDate = None,
      articleText = None, tags = Seq.empty, categories = Seq.empty,
      relatedArticles = Seq.empty, sourceArticles = Seq.empty)
  }
}
