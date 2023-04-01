package edumate.app.domain.repository

import edumate.app.domain.model.course_work.Link

interface JsoupRepository {
    suspend fun connect(url: String): Link
}