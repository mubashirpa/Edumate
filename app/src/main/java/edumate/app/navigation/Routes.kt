package edumate.app.navigation

object Routes {
    object Screen {
        const val CREATE_CLASS_SCREEN = "create_class_screen"
        const val GET_STARTED_SCREEN = "get_started_screen"
        const val HOME_SCREEN = "home_screen"
        const val LOGIN_SCREEN = "login_screen"
        const val RECOVER_SCREEN = "recover_screen"
        const val REGISTER_SCREEN = "register_screen"
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