package repository

import dto.RepoDTO
import dto.crawlresult.CrawlResult
import org.apache.commons.csv._

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Path

class OneCsvRepo(targetPath: Path) extends Repository {

  override def saveStep(stepResults: Seq[RepoDTO]): Unit = {
    RepoCommons.saveDtosToCsv(stepResults, targetPath)
  }
}
