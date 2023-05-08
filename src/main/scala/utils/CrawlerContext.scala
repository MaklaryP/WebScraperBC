package utils

import logger.{LogContext, LoggerInstance}
import net.ruippeixotog.scalascraper.model.Document
import repository.Repository
import urlmanager.UrlManager
import dto.Url.Url
import dto.crawlresult.CrawlResult

//Should encapsulate all side effects
case class CrawlerContext(crawlUrlFun: Url => CrawlResult,
                          urlQueue: UrlManager,
                          repo: Repository,
                          logCtx: LogContext = LogContext()
                         )
