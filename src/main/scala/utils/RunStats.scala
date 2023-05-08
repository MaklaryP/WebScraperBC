package utils

case class RunStats(numOfCrawled: BigInt, numOfFailed: BigInt){
  def doneNothing(): Boolean = numOfCrawled == 0 && numOfFailed == 0


  def ++(o: RunStats): RunStats = {
    RunStats(
      numOfCrawled + o.numOfCrawled,
      numOfFailed + o.numOfFailed
    )
  }

}
