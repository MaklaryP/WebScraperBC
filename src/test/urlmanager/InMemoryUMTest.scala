package urlmanager
import dto.Url.Url

class InMemoryUMTest extends UrlManagerCommonBehaviourTest {
  override protected def getUmImpl(toCrawl: Seq[Url], alreadyVisited: Set[Url]): UrlManager = InMemoryUM.create(toCrawl, alreadyVisited)

  override protected def getUmImpl(): UrlManager = new InMemoryUM()



}
