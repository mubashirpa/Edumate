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
}

object Server {
    const val API_BASE_URL = "http://localhost:8080/v1"
    const val ENDPOINT_COURSES = "courses"
    const val ENDPOINT_NOTIFICATION = "notification"

    object Parameters {
        const val COURSE_STATES = "courseStates"
        const val PAGE_SIZE = "pageSize"
        const val PAGE_TOKEN = "pageToken"
        const val STUDENT_ID = "studentId"
        const val TEACHER_ID = "teacherId"
    }
}
