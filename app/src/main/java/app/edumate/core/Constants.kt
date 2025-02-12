package app.edumate.core

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val BACKDROP_GET_STARTED =
        "https://firebasestorage.googleapis.com/v0/b/edu-mate-app.appspot.com/o/get_started.jpg?alt=media&token=6b9e6215-c0a4-4046-a15d-42cb5a102986"
    const val BACKDROP_GET_STARTED_LOCAL = "file:///android_asset/images/get_started.png"
    const val EDUMATE_BASE_URL = "https://edumate-learning.web.app/"
    const val WEB_GOOGLE_CLIENT_ID =
        "242656945367-ipk4n5tmvqgt9p8kqb9bk0dckgc8lefn.apps.googleusercontent.com"
    const val ALL_NOTIFICATIONS_CHANNEL_ID = "all_notifications"

    object Lottie {
        const val ANIM_VERIFY_EMAIL =
            "https://lottie.host/2c4c5083-70c9-4f92-b0e0-0836850114b4/m3BgaHNxoy.lottie"
        const val ANIM_CLASSWORK_SCREEN_EMPTY =
            "https://assets4.lottiefiles.com/packages/lf20_ikvz7qhc.json"
        const val ANIM_PEOPLE_SCREEN_EMPTY =
            "https://assets5.lottiefiles.com/private_files/lf30_TBKozE.json"
        const val ANIM_STREAM_SCREEN_EMPTY =
            "https://assets4.lottiefiles.com/packages/lf20_hxart9lz.json"
    }
}

object PreferencesKeys {
    val APP_THEME = stringPreferencesKey("app_theme")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
}

object Authentication {
    object Metadata {
        const val AVATAR_URL = "avatar_url"
        const val NAME = "name"
    }
}

object Supabase {
    object Column {
        const val ASSIGNED_GRADE = "assigned_grade"
        const val ASSIGNMENT_SUBMISSION = "assignment_submission"
        const val COURSE_ID = "course_id"
        const val CREATION_TIME = "creation_time"
        const val DESCRIPTION = "description"
        const val DUE_TIME = "due_time"
        const val ID = "id"
        const val JOINED_AT = "joined_at"
        const val MATERIALS = "materials"
        const val MAX_POINTS = "max_points"
        const val MULTIPLE_CHOICE_QUESTION = "multiple_choice_question"
        const val MULTIPLE_CHOICE_SUBMISSION = "multiple_choice_submission"
        const val NAME = "name"
        const val ROLE = "role"
        const val ROOM = "room"
        const val SECTION = "section"
        const val SHORT_ANSWER_SUBMISSION = "short_answer_submission"
        const val STATE = "state"
        const val SUBJECT = "subject"
        const val TEXT = "text"
        const val TITLE = "title"
        const val UPDATE_TIME = "update_time"
        const val USER_ID = "user_id"
    }

    object Function {
        const val GET_ANNOUNCEMENTS = "get_announcements"
        const val GET_ANNOUNCEMENT_COMMENTS = "get_announcement_comments"
        const val GET_STUDENT_SUBMISSION = "get_or_insert_student_submission"
        const val GET_STUDENT_SUBMISSIONS_LIST = "get_student_submissions_list"
        const val GET_SUBMISSION_COMMENTS = "get_submission_comments"
        const val INSERT_ANNOUNCEMENT_COMMENT = "insert_announcement_comment"
        const val INSERT_MEMBER = "insert_member"
        const val INSERT_SUBMISSION_COMMENT = "insert_submission_comment"
        const val TURN_IN_STUDENT_SUBMISSION = "turn_in_student_submission"
    }

    object Parameter {
        const val ANNOUNCEMENT_ID = "p_announcement_id"
        const val COURSE_ID = "p_course_id"
        const val COURSE_WORK_ID = "p_course_work_id"
        const val ID = "p_id"
        const val SUBMISSION_ID = "p_submission_id"
        const val TEXT = "p_text"
        const val USER_ID = "p_user_id"
    }

    object Storage {
        const val MATERIALS_BUCKET_ID = "materials"
    }

    object Table {
        const val ANNOUNCEMENTS = "announcements"
        const val COMMENTS = "comments"
        const val COURSES = "courses"
        const val COURSE_WORKS = "course_works"
        const val MEMBERS = "members"
        const val STUDENT_SUBMISSIONS = "student_submissions"
    }
}

object Navigation {
    object Args {
        const val CREATE_COURSE_SUCCESS = "create_course_success"
        const val CREATE_COURSE_WORK_SUCCESS = "create_course_work_success"
        const val UPDATE_COURSE_SETTINGS_SUCCESS = "update_course_settings_success"
    }
}
