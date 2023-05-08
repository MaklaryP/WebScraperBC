import dto.crawlresult.{Crawled, Failed}
import logger.{CLogger, ConsolePrintLogger, FileLogger, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import repository.{FolderCsvRepo, OneCsvRepo}
import urlmanager.InMemoryUM
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport}
import utils.CustomTypes._
import dto.Url.Url

import scala.concurrent.duration._
import java.nio.file.Path


object Main extends App{

  val browser = JsoupBrowser()

  //    val pathStr = "C:\\Users\\peter.maklary\\Documents\\PARA-Work\\Projects\\WebScraperBC\\data\\fileDb.csv"
//  val pathStr = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data\\fileDb.csv"
  val pathStr = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data\\repoFolder"

  val logPath = "C:\\Users\\peter\\Documents\\AAAA-PARA-PPC\\Projects\\Bakalarka\\data\\logs.txt"


  CLogger.setLogger(new FileLogger(LogLevel.DEBUG, Path.of(logPath)))
  val crawlerCtx = CrawlerContext(
    (url: Url) => browser.get(url),
//    new ConsolePrintLogger(LogLevel.DEBUG),
    new InMemoryUM(),
    new FolderCsvRepo(Path.of(pathStr))
  )

  val crawler =  new MyCrawler(crawlerCtx)
  val rep: CrawlerRunReport  = crawler.crawlMainJob(
    Seq(
      "https://index.sme.sk/c/23152259/koniec-banictva-na-hornej-nitre-maju-vyriesit-eurofondy-dotacie-vsak-remisova-stale-nespustila.html"
      ,"xxxxx"
      ,"yyyyy"
    ),
//    CrawlLimit.StepLimitedCrawl(2)
//    CrawlLimit.InfiniteCrawl()
    CrawlLimit.TimeLimitedCrawl(8.seconds.fromNow)
  )

  println(rep)
  CLogger.getLogger.log(rep.toString, LogLevel.INFO)
}
