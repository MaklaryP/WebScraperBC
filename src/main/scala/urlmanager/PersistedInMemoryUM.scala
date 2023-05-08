package urlmanager
import dto.Url.Url
import dto.UrlVisitRecord

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import java.nio.file.Path

class PersistedInMemoryUM(pathToSerialized: Path, private var inMemoryUM: InMemoryUM = new InMemoryUM()) extends UrlManager {

  def saveToFile(): Unit = {
    val file = pathToSerialized.toFile
    val fs = new FileOutputStream(file)
    try{
      val out = new ObjectOutputStream(fs)
      out.writeObject(inMemoryUM)
      out.flush()
      out.close()
    }catch{
      case _: Throwable => fs.close()
    }

  }

  def loadFromFile(): Unit = {
    val file = pathToSerialized.toFile
    val fs = new FileInputStream(file)
    try {
      val in = new ObjectInputStream(fs)
      inMemoryUM = in.readObject().asInstanceOf[InMemoryUM]
      in.close()
    }catch {
      case _: Throwable => fs.close()
    }
  }

  override def upsert(toUpsert: Seq[Url]): Unit = {
    inMemoryUM.upsert(toUpsert)
    saveToFile()
  }

  override def getBatch(batchSize: Int): Seq[Url] = {
    inMemoryUM.getBatch(batchSize)
  }

  override def markAsCrawled(crawled: Seq[UrlVisitRecord]): Unit = {
    inMemoryUM.markAsCrawled(crawled)
    saveToFile()
  }

  override def sizeToCrawl: Long = inMemoryUM.sizeToCrawl

  override def sizeOfCrawled: Long = inMemoryUM.sizeOfCrawled
}
