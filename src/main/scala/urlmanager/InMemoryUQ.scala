package urlmanager

import utils.CustomTypes.Url

class InMemoryUQ(private val q: Map[Url, VisitRecord]) extends UrlManager {

  def this() = this(Map.empty)

  override def upsert(toUpsert: Seq[VisitRecord]): UrlManager = {
    new InMemoryUQ(q ++ toUpsert.map(r => r.url -> r))
  }

  override def toCrawl(limit: Int): Iterable[VisitRecord] = {
    q.filter(r => r._2.state == VisitState.Unvisited).take(limit).values
  }

  override def size: Long = q.size

}
