package utils

import logger.Logger
import net.ruippeixotog.scalascraper.model.Document
import urlmanager.UrlManager
import utils.Url.Url

//Should encapsulate all side effects
case class CrawlerContext(getDocument: Url => Document, logger: Logger,
                                         urlQueue: UrlManager
//                          , repo: Repository //todo add repo to the context
                         )
