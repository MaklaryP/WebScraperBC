package dto

case class RepoDTO(state: String, url: String, error_msg: String,
                   title: String, authors: Seq[String],
                   intro_section: String, article_text: String,
                   publish_date: String, lst_upd_dt: String,
                   child_urls: Seq[String])

