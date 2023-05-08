package repository

import dto.RepoDTO
import logger.CLogger
import org.apache.commons.csv.{CSVFormat, CSVPrinter}

import java.io.FileWriter
import java.nio.file.Path

object RepoCommons {


  def saveDtosToCsv(dtos: Seq[RepoDTO], path: Path): Unit = {

    CLogger.getLogger.log(s"Writing to a file: ${path.toString} -- Start")

    val writer = new FileWriter(path.toFile, true)
    val printer = new CSVPrinter(writer, CSVFormat.DEFAULT)

    dtos.foreach { dto =>
      printer.printRecord(RepoCommons.repoDtoToSeqMapper(dto): _*)
    }

    printer.flush()
    writer.close()

    CLogger.getLogger.log(s"Writing to a file: ${path.toString} -- End")

  }


  def repoDtoToSeqMapper(repoDTO: RepoDTO): Seq[String] = {
    Seq(
      repoDTO.state,
      repoDTO.url,
      repoDTO.error_msg,
      repoDTO.title,
      repoDTO.authors.mkString(", "),
      repoDTO.intro_section,
      repoDTO.article_text,
      repoDTO.publish_date,
      repoDTO.lst_upd_dt,
      repoDTO.child_urls.mkString(", ")
    )

  }

}
