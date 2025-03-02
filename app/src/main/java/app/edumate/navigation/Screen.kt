package app.edumate.navigation

import app.edumate.domain.model.courseWork.CourseWorkType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object SignIn : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data class ResetPassword(
        val email: String? = null,
    ) : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data class Home(
        val courseId: String?,
        @SerialName("code")
        val enrollmentCode: String? = null,
    ) : Screen()

    @Serializable
    data class CreateCourse(
        val courseId: String? = null,
    ) : Screen()

    @Serializable
    data class CourseDetails(
        val courseId: String,
    ) : Screen()

    @Serializable
    data class Stream(
        val courseId: String,
    ) : Screen()

    @Serializable
    data class CourseWork(
        val courseId: String,
    ) : Screen()

    @Serializable
    data class People(
        val courseId: String,
    ) : Screen()

    @Serializable
    data class CreateCourseWork(
        val courseId: String,
        val courseWorkType: CourseWorkType,
        val courseWorkId: String? = null,
    ) : Screen()

    @Serializable
    data class ViewCourseWork(
        val courseId: String,
        val courseWorkId: String,
        val isCurrentUserStudent: Boolean,
    ) : Screen()

    @Serializable
    data class ViewStudentSubmission(
        val courseId: String,
        val courseWorkId: String,
        val studentId: String,
    ) : Screen()

    @Serializable
    data class ImageViewer(
        val imageUrl: String,
        val imageTitle: String? = null,
    ) : Screen()

    @Serializable
    data class PdfViewer(
        val pdfUrl: String,
        val pdfTitle: String? = null,
    ) : Screen()
}

@Serializable
sealed class Graph {
    @Serializable
    data object Authentication : Graph()
}
