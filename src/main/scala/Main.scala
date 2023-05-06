import crawlresult.{Crawled, Failed}
import logger.{ConsolePrintLogger, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import repository.OneCsvRepo
import urlmanager.InMemoryUM
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport}
import utils.CustomTypes._
import utils.Url.Url

import java.nio.file.Path


object Main extends App{

  val browser = JsoupBrowser()

  //    val pathStr = "C:\\Users\\peter.maklary\\Documents\\PARA-Work\\Projects\\WebScraperBC\\data\\fileDb.txt"
  val pathStr = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data\\fileDb.txt"

  val crawlerCtx = CrawlerContext(
    (url: Url) => browser.get(url),
    new ConsolePrintLogger(LogLevel.DEBUG),
    new InMemoryUM(),
    new OneCsvRepo(Path.of(pathStr))
  )
  val crawler =  new MyCrawler(crawlerCtx)
  val rep: CrawlerRunReport  = crawler.crawlMainJob(
    Seq(
      "https://index.sme.sk/c/23152259/koniec-banictva-na-hornej-nitre-maju-vyriesit-eurofondy-dotacie-vsak-remisova-stale-nespustila.html"
      ,"xxxxx"
      ,"yyyyy"
    ),
    CrawlLimit.StepLimitedCrawl(2)
//    CrawlLimit.InfiniteCrawl()
  )

  println(rep)
}
