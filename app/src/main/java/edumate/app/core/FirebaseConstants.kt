package edumate.app.core

object FirebaseConstants {

    object Firestore {
        // Collections
        const val COURSES_COLLECTION = "courses"
        const val COURSE_WORK_COLLECTION = "courseWork"
        const val USERS_COLLECTION = "users"
        const val STUDENT_SUBMISSIONS_COLLECTION = "studentSubmissions"

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

        // CourseWork
        const val COURSE_ID = "courseId"
        const val CREATOR_USER_ID = "creatorUserId"
        const val DUE_TIME = "dueTime"
        const val MATERIALS = "materials"
        const val MAX_POINTS = "maxPoints"
        const val MULTIPLE_CHOICE_QUESTION = "multipleChoiceQuestion"
        const val STATE = "state"
        const val SCHEDULED_TIME = "scheduledTime"
        const val TITLE = "title"
        const val WORK_TYPE = "workType"

        // StudentSubmissions
        const val COURSE_WORK_ID = "courseWorkId"
        const val USER_ID = "userId"
        const val LATE = "late"
        const val ASSIGNED_GRADE = "assignedGrade"
        const val COURSE_WORK_TYPE = "courseWorkType"
        const val ASSIGNMENT_SUBMISSION = "assignmentSubmission"
        const val SHORT_ANSWER_SUBMISSION = "shortAnswerSubmission"
        const val MULTIPLE_CHOICE_SUBMISSION = "multipleChoiceSubmission"

        // Users
        const val CREATED_AT = "createdAt"
        const val DISPLAY_NAME = "displayName"
        const val EMAIL_ADDRESS = "emailAddress"
        const val PHOTO_URL = "photoUrl"
        const val ENROLLED = "enrolled"
        const val TEACHING = "teaching"
        const val VERIFIED = "verified"
    }

    object Storage {
        const val COURSE_STORAGE_PATH = "course"
    }
}