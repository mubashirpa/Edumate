package app.edumate.data.mapper

import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.comment.CommentsDto
import app.edumate.domain.model.comment.Comment

fun CommentsDto.toComment(): Comment =
    Comment(
        creationTime = comment?.creationTime,
        creator = comment?.creator?.toUserDomainModel(),
        creatorUserId = comment?.creatorUserId,
        id = comment?.id,
        text = comment?.text,
        updateTime = comment?.updateTime,
    )

fun CommentDto.toComment(): Comment =
    Comment(
        creationTime = creationTime,
        creator = creator?.toUserDomainModel(),
        creatorUserId = creatorUserId,
        id = id,
        text = text,
        updateTime = updateTime,
    )
