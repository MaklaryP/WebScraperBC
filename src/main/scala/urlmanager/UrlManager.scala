package urlmanager

import utils.CustomTypes.Url

abstract class UrlManager {

  def upsert(toUpsert: Seq[VisitRecord]): UrlManager

  def upsertUnvisited(toUpsert: Seq[Url]): UrlManager = {
    upsert(toUpsert.map(VisitRecord(_, None, VisitState.Unvisited)))
  }

  def toCrawl(limit: Int): Iterable[VisitRecord]

  def toCrawlUrls(limit: Int): Iterable[Url] = toCrawl(limit).map(_.url)

  def size: Long

}
