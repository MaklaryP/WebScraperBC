package repository
import dto.RepoDTO

import scala.collection.mutable

class InMemoryRepo extends Repository {

  val mutableResult: mutable.Buffer[RepoDTO] = mutable.Buffer.empty[RepoDTO]

  override def saveStep(stepResults: Seq[RepoDTO]): Unit = {
    mutableResult.appendAll(stepResults)
  }

  def getAllSaved(): Seq[RepoDTO] = mutableResult.toSeq
}
