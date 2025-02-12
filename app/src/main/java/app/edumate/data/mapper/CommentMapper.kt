package app.edumate.data.mapper

import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.domain.model.comment.Comment

fun CommentDto.toComment(): Comment =
    Comment(
        courseId = courseId,
        creationTime = creationTime,
        creator = creator?.toUserDomainModel(),
        creatorUserId = creatorUserId,
        id = id,
        text = text,
        updateTime = updateTime,
    )
