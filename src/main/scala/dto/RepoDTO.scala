package dto

case class RepoDTO(state: String, url: String,
                   title: String, authors: Seq[String],
                   introSection: String, articleText: String,
                   publishDate: String, lastUpdatedDate: String,
                   childArticles: Seq[String])

