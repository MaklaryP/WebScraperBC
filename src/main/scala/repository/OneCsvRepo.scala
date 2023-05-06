package repository

import dto.RepoDTO
import dto.crawlresult.CrawlResult

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Path

class OneCsvRepo(targetPath: Path) extends Repository {
  override def saveStep(stepResults: Seq[RepoDTO]): Unit = {
    println("Writing....")
    val writer = new BufferedWriter(new FileWriter(targetPath.toFile, true))

    stepResults.foreach(r => writer.write(r.toString + "\n"))

    writer.flush() //We need to be sure that results are persisted before returning from this method
    writer.close()
    println("Finished Writing")
  }
}
