package dto.crawlresult

case class CrawlResult(crawled: Seq[Crawled] = Seq.empty, failed: Seq[Failed] = Seq.empty) {

  def addCrawled(c: Crawled): CrawlResult = {
    CrawlResult(crawled :+ c, failed)
  }

  def addFailed(f: Failed): CrawlResult = {
    CrawlResult(crawled, failed :+ f)
  }

  def ++(other: CrawlResult): CrawlResult = {
    CrawlResult(crawled ++ other.crawled, failed ++ other.failed)
  }
}
