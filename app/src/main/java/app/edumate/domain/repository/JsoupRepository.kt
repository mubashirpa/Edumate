package app.edumate.domain.repository

import app.edumate.data.remote.dto.material.LinkDto

interface JsoupRepository {
    suspend fun getUrlMetadata(url: String): LinkDto
}
