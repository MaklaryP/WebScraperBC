package domainscraper

import utils.CustomTypes.Url

object DomainFilter {

  def isUrlInSupportedDomains(url: Url): Boolean = {
    SupportedDomains().map(dom => dom.isUrlInDomain _).exists(_(url))
  }

  def filterSupportedUrls(urls: Seq[Url]): Seq[Url] = {
    urls.filter(isUrlInSupportedDomains)
  }

  def getDomainScraper(url: Url): Option[DomainScraper] = {
    SupportedDomains().find(_.isUrlInDomain(url))
  }

}