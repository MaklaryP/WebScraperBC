package dto.crawlresult

import dto.{PageContent, UrlVisitRecord}
import dto.Url.Url

case class Crawled(urlVisitRecord: UrlVisitRecord, linksOnPage: Seq[Url], pageContent: PageContent)