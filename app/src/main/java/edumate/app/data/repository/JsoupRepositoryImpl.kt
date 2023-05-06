package edumate.app.data.repository

import edumate.app.domain.model.Link
import edumate.app.domain.repository.JsoupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class JsoupRepositoryImpl : JsoupRepository {

    override suspend fun connect(url: String): Link {
        return withContext(Dispatchers.IO) {
            val document: Document = Jsoup.connect(url).get()
            val title: String = document.title().ifEmpty { url }
            val thumbnailUrl: String? = document.select("meta[property=og:image]").attr("content")
            Link(url = url, title = title, thumbnailUrl = thumbnailUrl)
        }
    }
}