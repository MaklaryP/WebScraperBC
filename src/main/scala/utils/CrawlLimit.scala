package utils

import org.joda.time.LocalDateTime

import scala.concurrent.duration.Deadline



object CrawlLimit{
  trait CrawlLimit {

    def shouldStopCrawling(numOfSteps: BigInt): Boolean

  }

  case class StepLimitedCrawl(maxSteps: BigInt) extends CrawlLimit {
    def stepLimitedCrawl(maxSteps: Int): StepLimitedCrawl = StepLimitedCrawl(BigInt(maxSteps))

    override def shouldStopCrawling(numOfSteps: BigInt): Boolean = numOfSteps > maxSteps
  }

  case class TimeLimitedCrawl(deadline: Deadline) extends CrawlLimit {
    override def shouldStopCrawling(numOfSteps: BigInt): Boolean = {
      deadline.isOverdue()
    }
  }

  case class InfiniteCrawl() extends CrawlLimit {
    override def shouldStopCrawling(numOfSteps: BigInt): Boolean = false
  }
}

