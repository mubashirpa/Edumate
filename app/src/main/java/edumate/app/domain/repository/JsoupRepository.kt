package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.Link

interface JsoupRepository {
    /**
     * Retrieves metadata such as the page title and thumbnail URL from the webpage.
     * @param url The URL of the webpage to connect to.
     * @return An instance of [Link]
     */
    suspend fun connect(url: String): Link
}
