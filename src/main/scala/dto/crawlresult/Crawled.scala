package dto.crawlresult

import dto.{PageContent, UrlVisitRecord}
import dto.Url.Url

case class Crawled(urlVisitRecord: UrlVisitRecord, supportedUrlsOnPage: Seq[Url], pageContent: PageContent)