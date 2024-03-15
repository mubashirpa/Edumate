package edumate.app.data.mapper

import edumate.app.core.utils.enumValueOf
import edumate.app.data.remote.dto.classroom.courseWork.Assignment
import edumate.app.data.remote.dto.classroom.courseWork.CourseWork
import edumate.app.data.remote.dto.classroom.courseWork.DueDate
import edumate.app.data.remote.dto.classroom.courseWork.DueTime
import edumate.app.data.remote.dto.classroom.courseWork.GradeCategory
import edumate.app.data.remote.dto.classroom.courseWork.MultipleChoiceQuestion
import edumate.app.domain.model.classroom.courseWork.Assignment as AssignmentDomainModel
import edumate.app.domain.model.classroom.courseWork.CourseWork as CourseWorkDomainModel
import edumate.app.domain.model.classroom.courseWork.DueDate as DueDateDomainModel
import edumate.app.domain.model.classroom.courseWork.DueTime as DueTimeDomainModel
import edumate.app.domain.model.classroom.courseWork.GradeCategory as GradeCategoryDomainModel
import edumate.app.domain.model.classroom.courseWork.MultipleChoiceQuestion as MultipleChoiceQuestionDomainModel

fun CourseWork.toCourseWorkDomainModel(): CourseWorkDomainModel {
    return CourseWorkDomainModel(
        alternateLink = alternateLink,
        assignment = assignment?.toAssignmentDomainModel(),
        creationTime = creationTime,
        description = description,
        dueDate = dueDate?.toDueDateDomainModel(),
        dueTime = dueTime?.toDueTimeDomainModel(),
        gradeCategory = gradeCategory?.toGradeCategoryDomainModel(),
        id = id,
        materials = materials?.map { it.toMaterialDomainModel() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestionDomainModel(),
        scheduledTime = scheduledTime,
        state = enumValueOf(state?.name),
        submissionModificationMode = enumValueOf(submissionModificationMode?.name),
        title = title,
        topicId = topicId,
        updateTime = updateTime,
        workType = enumValueOf(workType?.name),
    )
}

fun CourseWorkDomainModel.toCourseWork(): CourseWork {
    return CourseWork(
        alternateLink = alternateLink,
        assignment = assignment?.toAssignment(),
        creationTime = creationTime,
        description = description,
        dueDate = dueDate?.toDueDate(),
        dueTime = dueTime?.toDueTime(),
        gradeCategory = gradeCategory?.toGradeCategory(),
        id = id,
        materials = materials?.map { it.toMaterial() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestion(),
        scheduledTime = scheduledTime,
        state = enumValueOf(state?.name),
        submissionModificationMode = enumValueOf(submissionModificationMode?.name),
        title = title,
        topicId = topicId,
        updateTime = updateTime,
        workType = enumValueOf(workType?.name),
    )
}

private fun Assignment.toAssignmentDomainModel(): AssignmentDomainModel {
    return AssignmentDomainModel(
        studentWorkFolder = studentWorkFolder?.toDriveFolderDomainModel(),
    )
}

private fun AssignmentDomainModel.toAssignment(): Assignment {
    return Assignment(
        studentWorkFolder = studentWorkFolder?.toDriveFolder(),
    )
}

private fun DueDate.toDueDateDomainModel(): DueDateDomainModel {
    return DueDateDomainModel(
        day = day,
        month = month,
        year = year,
    )
}

private fun DueDateDomainModel.toDueDate(): DueDate {
    return DueDate(
        day = day,
        month = month,
        year = year,
    )
}

private fun DueTime.toDueTimeDomainModel(): DueTimeDomainModel {
    return DueTimeDomainModel(
        hours = hours,
        minutes = minutes,
        nanos = nanos,
        seconds = seconds,
    )
}

private fun DueTimeDomainModel.toDueTime(): DueTime {
    return DueTime(
        hours = hours,
        minutes = minutes,
        nanos = nanos,
        seconds = seconds,
    )
}

private fun GradeCategory.toGradeCategoryDomainModel(): GradeCategoryDomainModel {
    return GradeCategoryDomainModel(
        defaultGradeDenominator = defaultGradeDenominator,
        id = id,
        name = name,
        weight = weight,
    )
}

private fun GradeCategoryDomainModel.toGradeCategory(): GradeCategory {
    return GradeCategory(
        defaultGradeDenominator = defaultGradeDenominator,
        id = id,
        name = name,
        weight = weight,
    )
}

private fun MultipleChoiceQuestion.toMultipleChoiceQuestionDomainModel(): MultipleChoiceQuestionDomainModel {
    return MultipleChoiceQuestionDomainModel(
        choices = choices,
    )
}

private fun MultipleChoiceQuestionDomainModel.toMultipleChoiceQuestion(): MultipleChoiceQuestion {
    return MultipleChoiceQuestion(
        choices = choices,
    )
}
