package repository

import crawlresult.CrawlResult

trait Repository {

  def saveStep(stepResults: Seq[CrawlResult]): Unit

}
