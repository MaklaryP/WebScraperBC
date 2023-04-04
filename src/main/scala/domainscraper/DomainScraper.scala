package domainscraper

import net.ruippeixotog.scalascraper.model.Document
import utils.Result.{PageContent, Url}


trait DomainScraper {

  def parseDocument(doc: Document): (PageContent, Seq[Url])

  def isUrlInDomain(url: Url): Boolean

}
