package urlmanager

import dto.UrlVisitRecord
import urlmanager.InMemoryUM.removeDuplicates
import dto.Url.Url

class InMemoryUM private (private val q: Seq[Url], private val crawled: Set[Url]) extends UrlManager {

  def this() = this(Seq.empty, Set.empty)

  override def upsert(toUpsert: Seq[Url]): UrlManager = {
    new InMemoryUM(q ++ removeDuplicates(toUpsert, crawled), crawled)
  }

  override def getBatch(batchSize: Int): Seq[Url] = {
    q.take(batchSize)
  }

  override def markAsCrawled(toMark: Seq[UrlVisitRecord]): UrlManager = {
    val newCrawled = crawled ++ toMark.map(_.url).toSet
    new InMemoryUM(removeDuplicates(q, newCrawled), newCrawled)
  }

  override def sizeToCrawl: Long = q.size

  override def sizeOfCrawled: Long = crawled.size
}

object InMemoryUM{

  private type Q = Seq[Url]
  private type Crawled = Set[Url]

  private def removeDuplicates(q: Q, crawled: Crawled): Q = {
    val n = q.toSet -- crawled
    n.toSeq
  }

  def create(toVisit: Q, crawled: Crawled): InMemoryUM = {
    new InMemoryUM(removeDuplicates(toVisit.toSet.toSeq, crawled), crawled)
  }

}