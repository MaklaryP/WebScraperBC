package utils

case class RunStats(numOfCrawled: BigInt, numOfFailed: BigInt){

  def ++(o: RunStats): RunStats = {
    RunStats(
      numOfCrawled + o.numOfCrawled,
      numOfFailed + o.numOfFailed
    )
  }

  def nothingNewCrawled(newRunStat: RunStats): Boolean = {
    this == newRunStat
  }

}
