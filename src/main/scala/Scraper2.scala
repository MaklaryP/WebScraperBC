//import scala.collection.mutable
//import scala.concurrent.{ExecutionContext, Future}
//import scala.util.{Failure, Success}
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//
//import ExecutionContext.Implicits.global
//
//case class Crawled(url: String, links: List[String])
//case class Failed(url: String, reason: String)
//case class Link(url: String)
//
//class Crawler {
//  private val maxPages = 100
//  private val visited = mutable.Set[String]()
//  private val queue = mutable.Queue[String]()
//
//  def start(url: String): Unit = {
//    queue.enqueue(url)
//    crawl()
//  }
//
//  private def crawl(): Unit = {
//    if (visited.size < maxPages && queue.nonEmpty) {
//      val url = queue.dequeue()
//      visited.add(url)
//
//      Future(crawlUrl(url)).onComplete {
//        case Success(Crawled(url, links)) =>
//          println(s"Processed: $url")
//          links.filter(!visited.contains(_)).foreach(queue.enqueue(_))
//          crawl()
//        case Success(Failed(url, reason)) =>
//          println(s"Failed to crawl $url: $reason")
//          crawl()
//        case Failure(exception) =>
//          println(s"Failed to crawl $url: ${exception.getMessage}")
//          crawl()
//      }
//    }
//  }
//
//  private def crawlUrl(url: String): Either[Failed, Crawled] = {
//    try {
//      val doc: Document = Jsoup.connect(url).get()
//      val links = doc.select("a[href]").iterator.toList.map(_.attr("abs:href"))
//      Right(Crawled(url, links))
//    } catch {
//      case e: Exception =>
//        Left(Failed(url, e.getMessage))
//    }
//  }
//}
//
////object Main extends App {
////  val crawler = new Crawler()
////  crawler.start("https://en.wikipedia.org/wiki/Scala_(programming_language)")
////}
