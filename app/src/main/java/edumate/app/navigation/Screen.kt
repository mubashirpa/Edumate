package edumate.app.navigation

sealed class Screen(val route: String) {
    object GetStartedScreen : Screen(Routes.Screen.GET_STARTED_SCREEN)
    object LoginScreen : Screen(Routes.Screen.LOGIN_SCREEN)
    object RegisterScreen : Screen(Routes.Screen.REGISTER_SCREEN)
    object RecoverScreen : Screen(Routes.Screen.RECOVER_SCREEN)
    object HomeScreen : Screen(Routes.Screen.HOME_SCREEN)

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
