package urlmanager

import dto.UrlVisitRecord
import dto.Url.Url


trait UrlManager {

  def upsert(toUpsert: Seq[Url]): UrlManager

  def getBatch(batchSize: Int): Seq[Url]

  def markAsCrawled(crawled: Seq[UrlVisitRecord]): UrlManager

  def sizeToCrawl: Long

  def sizeOfCrawled: Long

}
