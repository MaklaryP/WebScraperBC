import crawlresult.{Crawled, Failed}
import logger.{ConsolePrintLogger, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import urlmanager.InMemoryUQ
import utils.CrawlerContext
import utils.CustomTypes._
import utils.Url.Url


object Main extends App{

  val browser = JsoupBrowser()

  val crawlerCtx = CrawlerContext(
    (url: Url) => browser.get(url),
    new ConsolePrintLogger(LogLevel.DEBUG),
    new InMemoryUQ()
  )
  val crawler =  new MyCrawler(crawlerCtx)
  val r  = crawler.crawlMainJob(Seq(
    "https://index.sme.sk/c/23152259/koniec-banictva-na-hornej-nitre-maju-vyriesit-eurofondy-dotacie-vsak-remisova-stale-nespustila.html"
  ), 80)

  println("Num of Crrawled: " + r.count(_.isInstanceOf[Crawled]))
  println("Num of Failed: " + r.count(_.isInstanceOf[Failed]))

  println("END")
}
