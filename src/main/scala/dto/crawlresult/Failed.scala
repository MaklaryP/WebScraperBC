package dto.crawlresult

import dto.UrlVisitRecord

case class Failed(urlVisitRecord: UrlVisitRecord, errorMsg: String)
