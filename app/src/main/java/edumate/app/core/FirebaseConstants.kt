package edumate.app.core

object FirebaseConstants {

    object Firestore {
        const val USERS_COLLECTION = "users"
        const val ROOMS_COLLECTION = "rooms"

        // Users
        const val CREATED_AT = "createdAt"
        const val DISPLAY_NAME = "displayName"
        const val EMAIL = "email"
        const val PHOTO_URL = "photoUrl"
        const val STUDENT = "student"
        const val TEACHER = "teacher"

        // Rooms
        const val CREATED_BY = "createdBy"
        const val CREATION_DATE = "creationDate"
        const val DESCRIPTION = "description"
        const val ID = "id"
        const val LINK = "link"
        const val SECTION = "section"
        const val STUDENTS = "students"
        const val SUBJECT = "subject"
        const val TEACHERS = "teachers"
        const val TITLE = "title"
    }

    object DynamicLinks {
        const val DOMAIN_URI_PREFIX = "https://edumate.page.link"
    }

    object Hosting {
        const val EDU_MATE_APP = "https://edu-mate-app.web.app/room?id="
        const val EDUMATEAPP = "https://edumateapp.web.app/room?id="
    }
}