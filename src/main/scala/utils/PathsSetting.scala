package utils

import java.nio.file.Path

object PathsSetting {

  case class Paths(repoFolderPath: Path, singleCsvPath: Path, logPath: Path, umSer: Path)

  def getPaths: Paths = Paths(
    addRootPath("repoFolder"),
    addRootPath("fileDb.csv"),
    addRootPath("logs.txt"),
    addRootPath("um.ser")
  )

  def addRootPath(filePathStr: String): Path = {
    Path.of(getHomePcRootStr, filePathStr)
  }

  def getHomePcRootStr: String = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data"

}
