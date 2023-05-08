import crawler.MyCrawler
import dto.crawlresult.{Crawled, Failed}
import logger.{CLogger, ConsolePrintLogger, FileLogger, LogContext, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import repository.{FolderCsvRepo, OneCsvRepo}
import urlmanager.{InMemoryUM, PersistedInMemoryUM}
import utils.{CrawlLimit, CrawlerContext, CrawlerRunReport, PathsSetting}
import utils.CustomTypes._
import dto.Url.Url

import scala.concurrent.duration._
import java.nio.file.Path


object Main extends App{

  val browser = JsoupBrowser()

  val paths = PathsSetting.getPaths

  CLogger.setLogger(new FileLogger(LogLevel.DEBUG, paths.logPath))
  val crawlerCtx = CrawlerContext(
    MyCrawler.crawlUrl(MyCrawler.visitUrl(browser)),
    new PersistedInMemoryUM(paths.umSer),
    new FolderCsvRepo(paths.repoFolderPath),
    LogContext()
  )

  val crawler =  new MyCrawler()
  val rep: CrawlerRunReport  = crawler.crawlMainJob(
    crawlerCtx,
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
