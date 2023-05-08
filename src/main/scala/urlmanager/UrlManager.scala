package urlmanager

import dto.UrlVisitRecord
import dto.Url.Url


trait UrlManager {
  //Immutable for implementations used for testing


  def upsert(toUpsert: Seq[Url]): Unit

  def getBatch(batchSize: Int): Seq[Url]

  def markAsCrawled(crawled: Seq[UrlVisitRecord]): Unit

  def sizeToCrawl: Long

  def sizeOfCrawled: Long

}
