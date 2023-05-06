package repository

import dto.RepoDTO
import dto.crawlresult.CrawlResult

trait Repository {

  def saveStep(stepResults: Seq[RepoDTO]): Unit

}
