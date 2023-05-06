package repository

import dto.RepoDTO
import dto.crawlresult.CrawlResult
import org.apache.commons.csv._

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Path

class OneCsvRepo(targetPath: Path) extends Repository {
  override def saveStep(stepResults: Seq[RepoDTO]): Unit = {
    println("Writing....")
    val writer = new FileWriter(targetPath.toFile, true)
    val printer = new CSVPrinter(writer, CSVFormat.DEFAULT)

//    printer.printRecord(stepResults.head.productElementNames.toSeq: _*)
    stepResults.foreach { dto =>
      printer.printRecord(dto.productIterator.toSeq: _*)
    }

    printer.flush()
    writer.close()
    println("Finished Writing")
  }
}
