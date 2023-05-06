package urlmanager

import urlmanager.InMemoryUQ.removeCrawled
import utils.Url.Url
import utils.UrlVisitRecord

class InMemoryUQ private (private val q: Seq[Url], private val crawled: Set[Url]) extends UrlManager {

  def this() = this(Seq.empty, Set.empty)

  override def upsert(toUpsert: Seq[Url]): UrlManager = {
    //todo remove also dupl with q - first write test that will chatch it
    new InMemoryUQ(q ++ removeCrawled(toUpsert, crawled), crawled)
  }

  override def getBatch(batchSize: Int): Seq[Url] = {
    q.take(batchSize)
  }

  override def markAsCrawled(toMark: Seq[UrlVisitRecord]): UrlManager = {
    val newCrawled = crawled ++ toMark.map(_.url).toSet
    new InMemoryUQ(removeCrawled(q, newCrawled), newCrawled)
  }

  override def sizeToCrawl: Long = q.size

  override def sizeOfCrawled: Long = crawled.size
}

object InMemoryUQ{

  private type Q = Seq[Url]
  private type Crawled = Set[Url]

  private def removeCrawled(q: Q, crawled: Crawled): Q = {
    val n = q.toSet -- crawled
    n.toSeq
  }

}