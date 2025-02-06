package app.edumate.data.mapper

import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.comment.CommentsDto
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

fun CommentsDto.toComment(): Comment =
    Comment(
        courseId = comment?.courseId,
        creationTime = comment?.creationTime,
        creator = comment?.creator?.toUserDomainModel(),
        creatorUserId = comment?.creatorUserId,
        id = comment?.id,
        text = comment?.text,
        updateTime = comment?.updateTime,
    )
