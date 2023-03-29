import utils.Result

object Main extends App{



  val r = MyCrawler.crawl(Seq("https://www.sme.sk/"))


  println("\n\n\nVisited All:")
  r._1.toSeq.sorted.foreach(println)


//  println("\n\n\nResults:")
//  r._2.foreach(println)


}
