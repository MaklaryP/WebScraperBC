package repository

import dto.RepoDTO

import java.time.LocalDateTime

object RepoSchema {

  def repoDtoToSeqMapper(repoDTO: RepoDTO, feedTimestamp: LocalDateTime): Seq[String] = {
    Seq(
      repoDTO.state,
      repoDTO.url,
      repoDTO.error_msg,
      feedTimestamp.toString,
      repoDTO.title,
      repoDTO.authors.mkString(", "),
      repoDTO.intro_section,
      repoDTO.article_text,
      repoDTO.publish_date,
      repoDTO.lst_upd_dt,
      repoDTO.child_urls.mkString(", ")
    )

  }

  def csvHeader: Seq[String] = {
    Seq(
      "state",
      "url",
      "error_msg",
      "feed_timestamp",
      "title",
      "authors",
      "intro_section",
      "article_text",
      "publish_date",
      "lst_upd_dt",
      "child_urls"
    )
  }

}
