package urlmanager

import dto.UrlVisitRecord
import urlmanager.InMemoryUM.removeDuplicates
import dto.Url.Url

class InMemoryUM private (private var queue: Seq[Url], private var crawled: Set[Url]) extends UrlManager with Serializable{

  def this() = this(Seq.empty, Set.empty)

  override def upsert(toUpsert: Seq[Url]): Unit = {
    val n = queue ++ removeDuplicates(toUpsert, crawled)
    queue = n
  }

  override def getBatch(batchSize: Int): Seq[Url] = {
    queue.take(batchSize)
  }

  override def markAsCrawled(toMark: Seq[UrlVisitRecord]): Unit = {
    val newCrawled = crawled ++ toMark.map(_.url).toSet
    val newQueue = removeDuplicates(queue, newCrawled)

    crawled = newCrawled
    queue = newQueue
  }

  override def sizeToCrawl: Long = queue.size

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