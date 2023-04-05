import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import utils.{CrawlerContext, Result}

import scala.util.Try
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import logger.{ConsolePrintLogger, LogLevel}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import utils.CustomTypes._
import utils.Result._


object Main extends App{

  val browser = JsoupBrowser()
  val crawlerCtx = CrawlerContext(
    (url: Url) => browser.get(url),
    new ConsolePrintLogger(LogLevel.DEBUG)
  )
  val crawler =  new MyCrawler(crawlerCtx)
  val r: (Set[Url], Seq[Result]) = crawler.crawlMainJob(Seq(
    "https://index.sme.sk/c/23152259/koniec-banictva-na-hornej-nitre-maju-vyriesit-eurofondy-dotacie-vsak-remisova-stale-nespustila.html"
  ), 80)

  println("Num of Crrawled: " + r._2.count(_.isInstanceOf[Crawled]))
  println("Num of Failed: " + r._2.count(_.isInstanceOf[Failed]))

  println("END")
}
