package edumate.app.domain.repository

import edumate.app.domain.model.Link

interface JsoupRepository {
    suspend fun connect(url: String): Link
}
