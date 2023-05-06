package dto

import utils.CustomTypes._
import Url.Url

case class PageContent(title: Option[String], authors: Seq[String],
                       introSection: Option[String], articleText: Option[String],
                       publishDate: Option[String], lastUpdatedDate: Option[String],
                       childArticles: Seq[Url])

object PageContent {
  def getEmpty: PageContent = {
    PageContent(title = None, authors = Seq.empty,
      publishDate = None, lastUpdatedDate = None,
      articleText = None, introSection = None,
      childArticles = Seq.empty)
  }
}
