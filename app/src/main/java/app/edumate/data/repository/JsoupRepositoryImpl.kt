package app.edumate.data.repository

import app.edumate.data.remote.dto.material.LinkDto
import app.edumate.domain.repository.JsoupRepository
import org.jsoup.Jsoup

class JsoupRepositoryImpl : JsoupRepository {
    override suspend fun getUrlMetadata(url: String): LinkDto {
        val document = Jsoup.connect(url).get()
        val title = document.title().ifEmpty { url }
        val thumbnailUrl = document.select("meta[property=og:image]").attr("content")
        return LinkDto(thumbnailUrl = thumbnailUrl, title = title, url = url)
    }
}
