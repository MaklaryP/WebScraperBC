package urlmanager

import utils.Url.Url
import utils.UrlVisitRecord


trait UrlManager {

  def upsert(toUpsert: Seq[Url]): UrlManager

  def getBatch(batchSize: Int): Seq[Url]

  def markAsCrawled(crawled: Seq[UrlVisitRecord]): UrlManager

  def sizeToCrawl: Long

  def sizeOfCrawled: Long

}
