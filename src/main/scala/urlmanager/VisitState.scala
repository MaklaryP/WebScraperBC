package urlmanager

object VisitState extends Enumeration {
  type VisitState = Value
  val Success, Fail, Unvisited = Value
}
