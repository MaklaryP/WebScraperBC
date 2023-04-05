package utils

import logger.Logger
import net.ruippeixotog.scalascraper.model.Document
import utils.CustomTypes._

//Should encapsulate all side effects
case class CrawlerContext(getDocument: Url => Document, logger: Logger
//                          , repo: Repository //todo add repo to the context
                         )