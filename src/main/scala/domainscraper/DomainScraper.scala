package domainscraper

import dto.PageContent
import net.ruippeixotog.scalascraper.model.Document
import dto.Url.Url


trait DomainScraper {

  def parseDocument(doc: Document): (PageContent, Seq[Url])

  def isUrlInDomain(url: Url): Boolean

}
