package utils.domainscraper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Document
import utils.Result.{PageContent, Url}

import scala.util.Try

object SmeDomain extends DomainScraper {
  override def parseDocument(doc: Document): (PageContent, Seq[Url]) = {

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

    val publishDate = getStringField(parsedMap, "datePublished")
    val dateModified = getStringField(parsedMap, "datePublished")


    val cont = PageContent.getFromOptions(
      title.flatten, authors.getOrElse(Seq.empty), publishDate, dateModified,
      None, Seq.empty, Seq.empty, Seq.empty
    )

    val urlsOpts = doc >?> elementList("""a[href^="https"]""") >?> attr("href")("a")

    val urls = urlsOpts.map(_.filter(_.isDefined).map(_.get)).getOrElse(Seq.empty)

    cont -> urls
  }

  override def isUrlInDomain(url: Url): Boolean = {
    url.startsWith("https://index.sme.sk")
//    true
  }
}
