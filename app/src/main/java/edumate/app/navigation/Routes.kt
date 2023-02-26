package edumate.app.navigation

object Routes {
    object Screen {
        const val HOME_SCREEN = "home_screen"
        const val GET_STARTED_SCREEN = "get_started_screen"
        const val LOGIN_SCREEN = "login_screen"
        const val REGISTER_SCREEN = "register_screen"
        const val RECOVER_SCREEN = "recover_screen"
    }

    object Graph {
        const val AUTHENTICATION = "authentication"
    }

    object Args {
        // RecoverScreen
        const val RECOVER_EMAIL = "email"
        const val RECOVER_SCREEN = "?$RECOVER_EMAIL={$RECOVER_EMAIL}"
    }
}