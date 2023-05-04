package urlmanager

import utils.CustomTypes.Url

import java.time.LocalDateTime

case class VisitRecord(url: Url, lastVisited: Option[LocalDateTime], state: VisitState.VisitState)

object VisitRecord{
  def unvisited(url: Url): VisitRecord = VisitRecord(url, None, VisitState.Unvisited)
}
