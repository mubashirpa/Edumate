package edumate.app.domain.repository

import edumate.app.data.remote.dto.RoomsDto

interface RoomsRepository {
    suspend fun rooms(): List<RoomsDto>
    suspend fun add(roomsDto: RoomsDto): String
    suspend fun update(roomId: String, roomsDto: RoomsDto)
    suspend fun delete(roomId: String)
}