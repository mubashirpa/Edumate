package edumate.app.navigation

object Routes {
    object Screen {
        const val CLASS_DETAILS_SCREEN = "class_details_screen"
        const val CLASSWORK_SCREEN = "classwork_screen"
        const val CREATE_CLASS_SCREEN = "create_class_screen"
        const val CREATE_CLASSWORK_SCREEN = "create_classwork_screen"
        const val GET_STARTED_SCREEN = "get_started_screen"
        const val HOME_SCREEN = "home_screen"
        const val JOIN_CLASS_SCREEN = "join_class_screen"
        const val LOGIN_SCREEN = "login_screen"
        const val PEOPLE_SCREEN = "people_screen"
        const val PROFILE_SCREEN = "profile_screen"
        const val RECOVER_SCREEN = "recover_screen"
        const val REGISTER_SCREEN = "register_screen"
        const val STREAM_SCREEN = "stream_screen"
        const val VIEW_CLASSWORK_SCREEN = "view_classwork_screen"
        const val VIEW_STUDENT_WORK_SCREEN = "view_student_work_screen"
    }

    object Graph {
        const val AUTHENTICATION = "authentication"
    }

    object Args {
        // ClassDetailsScreen
        const val CLASS_DETAILS_COURSE_ID = "course_id"
        const val CLASS_DETAILS_DEFAULT_COURSE_ID = "-1"
        const val CLASS_DETAILS_SCREEN = "/{$CLASS_DETAILS_COURSE_ID}"

        // ClassworkScreen
        const val CLASSWORK_COURSE_ID = "course_id"

        // CreateClassScreen
        const val CREATE_CLASS_COURSE_ID = "course_id"
        const val CREATE_CLASS_SCREEN = "?$CREATE_CLASS_COURSE_ID={$CREATE_CLASS_COURSE_ID}"

        // CreateClassworkScreen
        const val CREATE_CLASSWORK_COURSE_ID = "course_id"
        const val CREATE_CLASSWORK_ID = "course_work_id"
        const val CREATE_CLASSWORK_TYPE = "course_work_type"
        const val CREATE_CLASSWORK_SCREEN =
            "/{$CREATE_CLASSWORK_COURSE_ID}/{$CREATE_CLASSWORK_ID}/{$CREATE_CLASSWORK_TYPE}"

        // PeopleScreen
        const val PEOPLE_COURSE_ID = "course_id"
        const val PEOPLE_COURSE_OWNER_ID = "owner_id"

        // RecoverScreen
        const val RECOVER_EMAIL = "email"
        const val RECOVER_SCREEN = "?$RECOVER_EMAIL={$RECOVER_EMAIL}"

        // ViewClassworkScreen
        const val VIEW_CLASSWORK_COURSE_ID = "course_id"
        const val VIEW_CLASSWORK_ID = "course_work_id"
        const val VIEW_CLASSWORK_TYPE = "course_work_type"
        const val VIEW_CLASSWORK_USER_TYPE = "user_type"
        const val VIEW_CLASSWORK_SCREEN =
            "/{$VIEW_CLASSWORK_COURSE_ID}/{$VIEW_CLASSWORK_ID}/{$VIEW_CLASSWORK_TYPE}/{$VIEW_CLASSWORK_USER_TYPE}"

        // ViewStudentWorkScreen
        const val VIEW_STUDENT_WORK_COURSE_ID = "course_id"
        const val VIEW_STUDENT_WORK_COURSE_WORK_ID = "course_work_id"
        const val VIEW_STUDENT_WORK_ID = "student_submission_id"
        const val VIEW_STUDENT_WORK_SCREEN =
            "/{$VIEW_STUDENT_WORK_COURSE_ID}/{$VIEW_STUDENT_WORK_COURSE_WORK_ID}/{$VIEW_STUDENT_WORK_ID}"
    }
}