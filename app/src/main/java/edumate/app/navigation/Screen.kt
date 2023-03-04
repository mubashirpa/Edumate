package edumate.app.navigation

sealed class Screen(val route: String) {
    object CreateClassScreen : Screen(Routes.Screen.CREATE_CLASS_SCREEN)
    object GetStartedScreen : Screen(Routes.Screen.GET_STARTED_SCREEN)
    object HomeScreen : Screen(Routes.Screen.HOME_SCREEN)
    object LoginScreen : Screen(Routes.Screen.LOGIN_SCREEN)
    object RecoverScreen : Screen(Routes.Screen.RECOVER_SCREEN)
    object RegisterScreen : Screen(Routes.Screen.REGISTER_SCREEN)

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withOptionalArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("$arg")
            }
        }
    }
}
