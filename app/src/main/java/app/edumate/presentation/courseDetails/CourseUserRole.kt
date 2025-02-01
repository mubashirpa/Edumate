package app.edumate.presentation.courseDetails

sealed class CourseUserRole {
    data class Teacher(
        val isCourseOwner: Boolean,
    ) : CourseUserRole()

    data object Student : CourseUserRole()
}
