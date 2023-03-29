//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//trait WebScraper {
//  def fetchUrl(url: String): Future[Document]
//
//  def extractTitle(doc: Document): String
//
//  def extractLinks(doc: Document): List[String]
//}
//
//object WebScraper {
//  def apply(): WebScraper = new WebScraper {
//    def fetchUrl(url: String): Future[Document] =
//      Future(Jsoup.connect(url).get())
//
//    def extractTitle(doc: Document): String = doc.title()
//
//    def extractLinks(doc: Document): List[String] =
//      doc.select("a[href]").asScala.map(_.attr("href")).toList
//  }
//
//  def main(args: Array[String]): Unit = {
//    val scraper = WebScraper()
//    val url = "https://www.example.com"
//
//    scraper.fetchUrl(url).onComplete {
//      case scala.util.Success(doc) =>
//        val title = scraper.extractTitle(doc)
//        val links = scraper.extractLinks(doc)
//
//        println(s"Title: $title")
//        println("Links:")
//        links.foreach(println)
//      case scala.util.Failure(exception) =>
//        println(s"Failed to fetch $url: $exception")
//    }
//  }
//}