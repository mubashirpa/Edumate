package edumate.app.core

object FirebaseConstants {

    object Firestore {
        // Collections
        const val COURSES_COLLECTION = "courses"
        const val USERS_COLLECTION = "users"

        // Courses
        const val ALTERNATE_LINK = "alternateLink"
        const val COURSE_STATE = "courseState"
        const val CREATION_TIME = "creationTime"
        const val DESCRIPTION = "description"
        const val DESCRIPTION_HEADING = "descriptionHeading"
        const val ID = "id"
        const val NAME = "name"
        const val OWNER_ID = "ownerId"
        const val ROOM = "room"
        const val SECTION = "section"
        const val STUDENTS = "students"
        const val SUBJECT = "subject"
        const val TEACHERS = "teachers"
        const val UPDATE_TIME = "updateTime"

        // Users
        const val CREATED_AT = "createdAt"
        const val DISPLAY_NAME = "displayName"
        const val EMAIL_ADDRESS = "emailAddress"
        const val PHOTO_URL = "photoUrl"
        const val ENROLLED = "enrolled"
        const val TEACHING = "teaching"
        const val VERIFIED = "verified"
    }

    object DynamicLinks {
        const val DOMAIN_URI_PREFIX = "https://edumate.page.link"
    }

    object Hosting {
        const val EDU_MATE_APP = "https://edu-mate-app.web.app/room?id="
        const val EDUMATEAPP = "https://edumateapp.web.app/room?id="
    }
}