package dto

import Url.Url

import java.time.LocalDateTime

case class UrlVisitRecord(url: Url, visitTimestamp: LocalDateTime)
