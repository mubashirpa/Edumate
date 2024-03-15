package edumate.app.core

object Constants {
    const val ANIM_CLASSWORK_SCREEN_EMPTY =
        "https://assets4.lottiefiles.com/packages/lf20_ikvz7qhc.json"
    const val ANIM_MEET_SCREEN_EMPTY =
        "https://assets1.lottiefiles.com/private_files/lf30_jrhj68re.json"
    const val ANIM_PEOPLE_SCREEN_EMPTY =
        "https://assets5.lottiefiles.com/private_files/lf30_TBKozE.json"
    const val ANIM_STREAM_SCREEN_EMPTY =
        "https://assets4.lottiefiles.com/packages/lf20_hxart9lz.json"
    const val BACKDROP_GET_STARTED =
        "https://firebasestorage.googleapis.com/v0/b/edu-mate-app.appspot.com/o/get_started.jpg?alt=media&token=6b9e6215-c0a4-4046-a15d-42cb5a102986"
    const val BACKDROP_GET_STARTED_LOCAL = "file:///android_asset/images/get_started.png"
    const val GOOGLE_SERVER_CLIENT_ID =
        "397578092741-alqtcebud1r0tsddkm90gj3bfjebkdk0.apps.googleusercontent.com"
}

object Firebase {
    object Storage {
        private const val COURSE_WORK_PATH = "courses/{courseId}/courseWorks/{id}"
        const val COURSE_WORK_MATERIALS_PATH = "${COURSE_WORK_PATH}/materials"
        private const val ANNOUNCEMENTS_PATH = "courses/{courseId}/announcements/{id}"
        const val ANNOUNCEMENTS_MATERIALS_PATH = "${ANNOUNCEMENTS_PATH}/materials"
    }
}

object Server {
    const val API_BASE_URL = "http://192.168.2.121:8080/v1"
    const val ENDPOINT_ANNOUNCEMENTS = "announcements"
    const val ENDPOINT_COURSES = "courses"
    const val ENDPOINT_COURSE_WORK = "courseWork"
    const val ENDPOINT_NOTIFICATION = "notification"
    const val ENDPOINT_STUDENTS = "students"
    const val ENDPOINT_STUDENT_SUBMISSIONS = "studentSubmissions"
    const val ENDPOINT_TEACHERS = "teachers"

    object Parameters {
        const val ANNOUNCEMENT_STATES = "announcementStates"
        const val COURSE_STATES = "courseStates"
        const val COURSE_WORK_STATES = "courseWorkStates"
        const val ENROLLMENT_CODE = "enrollmentCode"
        const val LATE = "late"
        const val ORDER_BY = "orderBy"
        const val PAGE_SIZE = "pageSize"
        const val PAGE_TOKEN = "pageToken"
        const val RECLAIM = "reclaim"
        const val RETURN = "return"
        const val STATES = "states"
        const val STUDENT_ID = "studentId"
        const val TEACHER_ID = "teacherId"
        const val TURN_IN = "turnIn"
        const val USER_ID = "userId"
    }
}
