package utils.domainscraper

object SupportedDomains {

  val supportedDomains: Seq[DomainScraper] = Seq(
    SmeDomain
  )

  def apply(): Seq[DomainScraper] = {
    supportedDomains
  }

}
