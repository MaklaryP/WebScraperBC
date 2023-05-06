package mappers

import dto.RepoDTO
import dto.crawlresult.{Crawled, Failed}

object mappers {

  def crawledToRepoDTO(crawled: Crawled): RepoDTO = {
    RepoDTO("Crawled", crawled.urlVisitRecord.url)
  }

  def failedToRepoDTO(failed: Failed): RepoDTO = {
    RepoDTO("Failed", failed.urlVisitRecord.url)
  }

}
