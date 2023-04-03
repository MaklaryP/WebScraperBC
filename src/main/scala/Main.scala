import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import utils.Result

import scala.util.Try
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import utils.Result.{Crawled, Failed, Result, Url}


object Main extends App{

  val browser = JsoupBrowser()
  val crawler =  new MyCrawler((url: Url) => browser.get(url))
  val r: (Set[Url], Seq[Result]) = crawler.crawlMainJob(Seq(
    "https://index.sme.sk/c/23152259/koniec-banictva-na-hornej-nitre-maju-vyriesit-eurofondy-dotacie-vsak-remisova-stale-nespustila.html"
  ), 80)

  println("Num of Crrawled: " + r._2.count(_.isInstanceOf[Crawled]))
  println("Num of Failed: " + r._2.count(_.isInstanceOf[Failed]))

  println("END")
}
