import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import urlmanager._
import utils.PathsSetting

import java.nio.file.Path
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import dto.PageContent
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import org.jsoup.Jsoup


object Sandbox extends App {

//  val paths = PathsSetting.getPaths
//
//  val x = new InMemoryUM()
//  val um = new PersistedInMemoryUM(x, paths.umSer)
//  um.loadFromFile()

  val browser = new JsoupBrowser()

  val doc = browser.get("https://index.sme.sk/t/8630/oligarchovia")
//  select("div.mb-m.sans-reg.fs-14.lh-xl.mt-m.mt-xxs-mo")
  val r = doc >?> text("""div.mb-m.sans-reg.fs-14.lh-xl.mt-m.mt-xxs-mo p""")
//  val p = Jsoup.parse(doc.toHtml)
//  val r = p.select("div.mb-m.sans-reg.fs-14.lh-xl.mt-m.mt-xxs-mo")
  println(r)

}
