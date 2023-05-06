package utils


object CrawlLimit{
  trait CrawlLimit {

    def shouldStopCrawling(numOfSteps: BigInt): Boolean

  }

  case class StepLimitedCrawl(maxSteps: BigInt) extends CrawlLimit {
    def stepLimitedCrawl(maxSteps: Int): StepLimitedCrawl = StepLimitedCrawl(BigInt(maxSteps))

    override def shouldStopCrawling(numOfSteps: BigInt): Boolean = numOfSteps > maxSteps
  }

  case class InfiniteCrawl() extends CrawlLimit {
    override def shouldStopCrawling(numOfSteps: BigInt): Boolean = false
  }
}

