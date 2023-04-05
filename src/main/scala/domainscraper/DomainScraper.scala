package domainscraper

import net.ruippeixotog.scalascraper.model.Document
import utils.CustomTypes.Url
import utils.PageContent


trait DomainScraper {

  def parseDocument(doc: Document): (PageContent, Seq[Url])

  def isUrlInDomain(url: Url): Boolean

}
