package repository
import dto.RepoDTO
import org.apache.commons.csv.{CSVFormat, CSVPrinter}

import java.io.FileWriter
import java.nio.file.Path
import java.time.{LocalDateTime, ZoneOffset}

class FolderCsvRepo(folderPath: Path) extends Repository {


  override def saveStep(stepResults: Seq[RepoDTO]): Unit = {

//    val fileName = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString + ".csv"
    val fileName = LocalDateTime.now().toString.replaceAll("""\D""", "_") + ".csv"
    val path: Path = folderPath.resolve(fileName)

    RepoCommons.saveDtosToCsv(stepResults, path, Some(RepoSchema.csvHeader))
  }



}
