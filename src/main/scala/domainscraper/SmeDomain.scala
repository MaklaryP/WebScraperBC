package domainscraper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import dto.PageContent
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import dto.Url.Url
import scala.util.Try

object SmeDomain extends DomainScraper {
  override def parseDocument(doc: Document): PageContent = {

    val metadataElement = doc >?> elementList("""script[type="application/ld+json"]""")

    type AnyMap = Map[String, Any]
    val parsedMap = metadataElement
      .filter(_.size == 1)
      .map(_.head.innerHtml)
      .map(strJson => {
        val s = "{\"name\": \"educative\",\"website\": \"https://www.educative.io/\",\"description\": \"e-learning website\"}"
        val om = new ObjectMapper().registerModule(DefaultScalaModule)
        om.readValue(strJson, classOf[AnyMap])
      })

    def getStringField(jsonMapOpt: Option[AnyMap], fieldName: String): Option[String] = {
      jsonMapOpt.flatMap(_.get(fieldName)).map(_.asInstanceOf[String])
    }

    val title = doc >?> element("""meta[property="og:title"]""") >?> attr("content")
    val authors = Try{
      parsedMap.get("author").asInstanceOf[Seq[AnyMap]]
        .map(_("name").asInstanceOf[String])
    }.toOption

    val introOpt = (doc >?> text("""p.perex"""))
      .orElse(doc >?> text("""div.mb-m.sans-reg.fs-14.lh-xl.mt-m.mt-xxs-mo p"""))
    val articleOpt = doc >?> texts("""article p""")


    val publishDate = getStringField(parsedMap, "datePublished")
    val dateModified = getStringField(parsedMap, "datePublished")


    val cont = PageContent.getEmpty.copy(
      title.flatten, authors.getOrElse(Seq.empty), publishDate = publishDate, lastUpdatedDate = dateModified,
      articleText = articleOpt.map(_.mkString(",")),
      introSection = introOpt
    )

    val urlsOpts = doc >?> elementList("""a[href^="https"]""") >?> attr("href")("a")

    val urls = urlsOpts.map(_.filter(_.isDefined).map(_.get)).getOrElse(Seq.empty)

    cont.copy(childArticles = urls)
  }

  private val subDomains = Set(
    "https://index.sme.sk",
    "https://kultura.sme.sk",
    "https://primar.sme.sk",
    "https://korzar.sme.sk",
    "https://kosice.korzar.sme.sk",


//    "https://svet.sme.sk",
//    "https://zena.sme.sk",
//    "https://tech.sme.sk",
//    "https://auto.sme.sk",
//    "https://komentare.sme.sk",
//    "https://presov.sme.sk",
//    "https://mybystrica.sme.sk",
//    "https://myzvolen.sme.sk",
//    "https://myzilina.sme.sk",
//    "https://mytrencin.sme.sk",
//    "https://mynitra.sme.sk",
//    "https://bratislava.sme.sk",
//    "https://bratislava.sme.sk",

  )

  override def isUrlInDomain(url: Url): Boolean = {
    val domain = "sme.sk"
    val i = url.indexOf(domain)
    val prefix = url.substring(0, i + domain.length)

    subDomains.contains(prefix)
  }
}
