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
      printToCsv(RepoSchema.repoDtoToSeqMapper(dto, LocalDateTime.now()))
    }

    printer.flush()
    writer.close()

    CLogger.getLogger.log(s"Writing to a file: ${path.toString} -- End")

  }


}
