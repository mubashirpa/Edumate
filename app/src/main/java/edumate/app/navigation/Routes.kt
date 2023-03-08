package edumate.app.navigation

object Routes {
    object Screen {
        const val CLASS_DETAILS_SCREEN = "class_details_screen"
        const val CLASSWORK_SCREEN = "classwork_screen"
        const val CREATE_CLASS_SCREEN = "create_class_screen"
        const val GET_STARTED_SCREEN = "get_started_screen"
        const val HOME_SCREEN = "home_screen"
        const val JOIN_CLASS_SCREEN = "join_class_screen"
        const val LOGIN_SCREEN = "login_screen"
        const val PEOPLE_SCREEN = "people_screen"
        const val RECOVER_SCREEN = "recover_screen"
        const val REGISTER_SCREEN = "register_screen"
        const val STREAM_SCREEN = "stream_screen"
    }

    object Graph {
        const val AUTHENTICATION = "authentication"
    }

    object Args {
        // RecoverScreen
        const val RECOVER_EMAIL = "email"
        const val RECOVER_SCREEN = "?$RECOVER_EMAIL={$RECOVER_EMAIL}"

        // ClassDetailsScreen
        const val CLASS_DETAILS_COURSE_ID = "course_id"
        const val CLASS_DETAILS_DEFAULT_COURSE_ID = "-1"
        const val CLASS_DETAILS_SCREEN = "/{$CLASS_DETAILS_COURSE_ID}"

        // CreateClassScreen
        const val CREATE_CLASS_COURSE_ID = "course_id"
        const val CREATE_CLASS_SCREEN = "?$CREATE_CLASS_COURSE_ID={$CREATE_CLASS_COURSE_ID}"
    }
}