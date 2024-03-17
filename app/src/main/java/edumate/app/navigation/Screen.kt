package edumate.app.navigation

sealed class Screen(val route: String) {
    data object ClassDetailsScreen : Screen(Routes.Screen.CLASS_DETAILS_SCREEN)

    data object ClassworkScreen : Screen(Routes.Screen.CLASSWORK_SCREEN)

    data object CreateAnnouncementScreen : Screen(Routes.Screen.CREATE_ANNOUNCEMENT_SCREEN)

    data object CreateClassScreen : Screen(Routes.Screen.CREATE_CLASS_SCREEN)

    data object CreateClassworkScreen : Screen(Routes.Screen.CREATE_CLASSWORK_SCREEN)

    data object GetStartedScreen : Screen(Routes.Screen.GET_STARTED_SCREEN)

    data object HomeScreen : Screen(Routes.Screen.HOME_SCREEN)

    data object JoinClassScreen : Screen(Routes.Screen.JOIN_CLASS_SCREEN)

    data object LoginScreen : Screen(Routes.Screen.LOGIN_SCREEN)

    data object PeopleScreen : Screen(Routes.Screen.PEOPLE_SCREEN)

    data object ProfileScreen : Screen(Routes.Screen.PROFILE_SCREEN)

    data object RecoverScreen : Screen(Routes.Screen.RECOVER_SCREEN)

    data object RegisterScreen : Screen(Routes.Screen.REGISTER_SCREEN)

    data object SettingsScreen : Screen(Routes.Screen.SETTINGS_SCREEN)

    data object StreamScreen : Screen(Routes.Screen.STREAM_SCREEN)

    data object ViewClassworkScreen : Screen(Routes.Screen.VIEW_CLASSWORK_SCREEN)

    data object ViewStudentWorkScreen : Screen(Routes.Screen.VIEW_STUDENT_WORK_SCREEN)

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
