package app.edumate.navigation

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
        val email: String?,
    ) : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data class Home(
        val courseId: String?,
        @SerialName("code")
        val enrollmentCode: String? = null,
    ) : Screen()

    @Serializable
    data class CreateCourse(
        val courseId: String?,
    ) : Screen()
}

@Serializable
sealed class Graph {
    @Serializable
    data object Authentication : Graph()
}
