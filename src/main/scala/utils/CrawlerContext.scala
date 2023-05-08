package utils

import logger.{LogContext, LoggerInstance}
import net.ruippeixotog.scalascraper.model.Document
import repository.Repository
import urlmanager.UrlManager
import dto.Url.Url

//Should encapsulate all side effects
case class CrawlerContext(getDocument: Url => Document,
                          urlQueue: UrlManager,
                          repo: Repository,
                          logCtx: LogContext
                         )
