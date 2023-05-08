package mappers

import dto.RepoDTO
import dto.crawlresult.{Crawled, Failed}

import scala.language.implicitConversions

object mappers {

  def crawledToRepoDTO(crawled: Crawled): RepoDTO = {
    implicit def optToString(o: Option[String]): String = o.getOrElse("")

    val pc = crawled.pageContent
    RepoDTO("Crawled", crawled.urlVisitRecord.url, "",
      pc.title, pc.authors, pc.introSection, pc.articleText,
      pc.publishDate, pc.lastUpdatedDate, pc.childArticles)
  }

  def failedToRepoDTO(failed: Failed): RepoDTO = {
    RepoDTO("Failed", failed.urlVisitRecord.url, failed.errorMsg,
            "", Seq.empty, "", "", "", "", Seq.empty)
  }

}
