package utils

object Result {

  type Url = String
  case class PageContent()

//  case class PageId(id: String)
  //todo how to check pageId uniqueness - use enum

  trait Result

  case class Crawled(pageId: String, url: Url, linksOnPage: Seq[Url], pageContent: PageContent) extends Result
  case class Failed(pageId: String, url: Url, failReason: String) extends Result

  def getLinksOnPage(results: Seq[Result]): Seq[Url] = {
    results.foldLeft(Seq.empty[Url]){
      case (accSeq, Crawled(_, _, linksOnPage, _)) => accSeq ++ linksOnPage
      case (accSeq, _) => accSeq
    }
  }

}
