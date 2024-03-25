package edumate.app.data.mapper

import edumate.app.core.utils.enumValueOf
import edumate.app.data.remote.dto.classroom.studentSubmissions.AssignmentSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.Attachment
import edumate.app.data.remote.dto.classroom.studentSubmissions.ModifyAttachments
import edumate.app.data.remote.dto.classroom.studentSubmissions.MultipleChoiceSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.ShortAnswerSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmission
import edumate.app.domain.model.classroom.studentSubmissions.AssignmentSubmission as AssignmentSubmissionDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.Attachment as AttachmentDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.ModifyAttachments as ModifyAttachmentsDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.MultipleChoiceSubmission as MultipleChoiceSubmissionDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.ShortAnswerSubmission as ShortAnswerSubmissionDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.StudentSubmission as StudentSubmissionDomainModel

fun StudentSubmission.toStudentSubmissionDomainModel(): StudentSubmissionDomainModel {
    return StudentSubmissionDomainModel(
        alternateLink = alternateLink,
        assignedGrade = assignedGrade,
        assignmentSubmission = assignmentSubmission?.toAssignmentSubmissionDomainModel(),
        courseWorkType = enumValueOf(courseWorkType?.name),
        creationTime = creationTime,
        draftGrade = draftGrade,
        id = id,
        late = late,
        multipleChoiceSubmission = multipleChoiceSubmission?.toMultipleChoiceSubmissionDomainModel(),
        shortAnswerSubmission = shortAnswerSubmission?.toShortAnswerSubmissionDomainModel(),
        state = enumValueOf(state?.name),
        updateTime = updateTime,
        userId = userId,
    )
}

fun StudentSubmissionDomainModel.toStudentSubmission(): StudentSubmission {
    return StudentSubmission(
        alternateLink = alternateLink,
        assignedGrade = assignedGrade,
        assignmentSubmission = assignmentSubmission?.toAssignmentSubmission(),
        courseWorkType = enumValueOf(courseWorkType?.name),
        creationTime = creationTime,
        draftGrade = draftGrade,
        id = id,
        late = late,
        multipleChoiceSubmission = multipleChoiceSubmission?.toMultipleChoiceSubmission(),
        shortAnswerSubmission = shortAnswerSubmission?.toShortAnswerSubmission(),
        state = enumValueOf(state?.name),
        updateTime = updateTime,
        userId = userId,
    )
}

fun ModifyAttachmentsDomainModel.toModifyAttachments(): ModifyAttachments {
    return ModifyAttachments(
        addAttachments = addAttachments?.map { it.toAttachment() },
    )
}

private fun AssignmentSubmission.toAssignmentSubmissionDomainModel(): AssignmentSubmissionDomainModel {
    return AssignmentSubmissionDomainModel(
        attachments = attachments?.map { it.toAttachmentDomainModel() },
    )
}

private fun AssignmentSubmissionDomainModel.toAssignmentSubmission(): AssignmentSubmission {
    return AssignmentSubmission(
        attachments = attachments?.map { it.toAttachment() },
    )
}

private fun Attachment.toAttachmentDomainModel(): AttachmentDomainModel {
    return AttachmentDomainModel(
        driveFile = driveFile?.toDriveFileDomainModel(),
        link = link?.toLinkDomainModel(),
    )
}

private fun AttachmentDomainModel.toAttachment(): Attachment {
    return Attachment(
        driveFile = driveFile?.toDriveFile(),
        link = link?.toLink(),
    )
}

private fun MultipleChoiceSubmission.toMultipleChoiceSubmissionDomainModel(): MultipleChoiceSubmissionDomainModel {
    return MultipleChoiceSubmissionDomainModel(
        answer = answer,
    )
}

private fun MultipleChoiceSubmissionDomainModel.toMultipleChoiceSubmission(): MultipleChoiceSubmission {
    return MultipleChoiceSubmission(
        answer = answer,
    )
}

private fun ShortAnswerSubmission.toShortAnswerSubmissionDomainModel(): ShortAnswerSubmissionDomainModel {
    return ShortAnswerSubmissionDomainModel(
        answer = answer,
    )
}

private fun ShortAnswerSubmissionDomainModel.toShortAnswerSubmission(): ShortAnswerSubmission {
    return ShortAnswerSubmission(
        answer = answer,
    )
}
