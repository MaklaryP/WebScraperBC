package repository

import dto.RepoDTO
import logger.CLogger
import org.apache.commons.csv.{CSVFormat, CSVPrinter}

import java.io.FileWriter
import java.nio.file.Path
import java.time.LocalDateTime

object RepoCommons {



  def saveDtosToCsv(dtos: Seq[RepoDTO], path: Path, header: Option[Seq[String]] = None): Unit = {

    CLogger.getLogger.log(s"Writing to a file: ${path.toString} -- Start")

    val writer = new FileWriter(path.toFile, true)
    val printer = new CSVPrinter(writer, CSVFormat.DEFAULT)

    def printToCsv(s: Seq[String]): Unit = printer.printRecord(s: _*)

    header.foreach(printToCsv)

    dtos.foreach { dto =>
      printToCsv(RepoCommons.repoDtoToSeqMapper(dto, LocalDateTime.now()))
    }

    printer.flush()
    writer.close()

    CLogger.getLogger.log(s"Writing to a file: ${path.toString} -- End")

  }


  def repoDtoToSeqMapper(repoDTO: RepoDTO, feedTimestamp: LocalDateTime): Seq[String] = {
    Seq(
      repoDTO.state,
      repoDTO.url,
      repoDTO.error_msg,
      feedTimestamp.toString,
      repoDTO.title,
      repoDTO.authors.mkString(", "),
      repoDTO.intro_section,
      repoDTO.article_text,
      repoDTO.publish_date,
      repoDTO.lst_upd_dt,
      repoDTO.child_urls.mkString(", ")
    )

  }

  def getCsvHeader(): Seq[String] = {
    Seq(
      "state",
      "url",
      "error_msg",
      "feed_timestamp",
      "title",
      "authors",
      "intro_section",
      "article_text",
      "publish_date",
      "lst_upd_dt",
      "child_urls"
    )
  }

}
