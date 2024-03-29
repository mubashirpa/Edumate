package edumate.app.navigation

sealed class Screen(val route: String) {
    object ClassDetailsScreen : Screen(Routes.Screen.CLASS_DETAILS_SCREEN)
    object ClassworkScreen : Screen(Routes.Screen.CLASSWORK_SCREEN)
    object CreateAnnouncementScreen : Screen(Routes.Screen.CREATE_ANNOUNCEMENT_SCREEN)
    object CreateClassScreen : Screen(Routes.Screen.CREATE_CLASS_SCREEN)
    object CreateClassworkScreen : Screen(Routes.Screen.CREATE_CLASSWORK_SCREEN)
    object GetStartedScreen : Screen(Routes.Screen.GET_STARTED_SCREEN)
    object HomeScreen : Screen(Routes.Screen.HOME_SCREEN)
    object JoinClassScreen : Screen(Routes.Screen.JOIN_CLASS_SCREEN)
    object LoginScreen : Screen(Routes.Screen.LOGIN_SCREEN)
    object MeetScreen : Screen(Routes.Screen.MEET_SCREEN)
    object PeopleScreen : Screen(Routes.Screen.PEOPLE_SCREEN)
    object ProfileScreen : Screen(Routes.Screen.PROFILE_SCREEN)
    object RecoverScreen : Screen(Routes.Screen.RECOVER_SCREEN)
    object RegisterScreen : Screen(Routes.Screen.REGISTER_SCREEN)
    object SettingsScreen : Screen(Routes.Screen.SETTINGS_SCREEN)
    object StreamScreen : Screen(Routes.Screen.STREAM_SCREEN)
    object ViewClassworkScreen : Screen(Routes.Screen.VIEW_CLASSWORK_SCREEN)
    object ViewStudentWorkScreen : Screen(Routes.Screen.VIEW_STUDENT_WORK_SCREEN)

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}