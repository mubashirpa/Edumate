package edumate.app.core

object FirebaseConstants {

    object Database {
        const val ANNOUNCEMENTS_PATH = "announcements"
        const val MEETINGS_PATH = "meetings"

        // Announcements
        const val ALTERNATE_LINK = "alternateLink"
        const val ASSIGNEE_MODE = "assigneeMode"
        const val COURSE_ID = "courseId"
        const val CREATION_TIME = "creationTime"
        const val CREATOR_USER_ID = "creatorUserId"
        const val CREATOR_PROFILE = "creatorProfile"
        const val ID = "id"
        const val INDIVIDUAL_STUDENTS_OPTIONS = "individualStudentsOptions"
        const val MATERIALS = "materials"
        const val SCHEDULED_TIME = "scheduledTime"
        const val STATE = "state"
        const val TEXT = "text"
        const val UPDATE_TIME = "updateTime"

        // Meeting
        const val MEETING_ID = "meetingId"
        const val TITLE = "title"
    }

    object Firestore {
        // Collections
        const val COURSES_COLLECTION = "courses"
        const val COURSE_WORK_COLLECTION = "courseWork"
        const val STUDENT_SUBMISSIONS_COLLECTION = "studentSubmissions"
        const val USERS_COLLECTION = "users"

        // Courses
        const val ALTERNATE_LINK = "alternateLink"
        const val CALENDAR_ID = "calendarId"
        const val COURSE_GROUP_ID = "courseGroupId"
        const val COURSE_STATE = "courseState"
        const val CREATION_TIME = "creationTime"
        const val CREATOR_PROFILE = "creatorProfile"
        const val DESCRIPTION = "description"
        const val DESCRIPTION_HEADING = "descriptionHeading"
        const val ENROLLMENT_CODE = "enrollmentCode"
        const val GRADE_BOOK_SETTINGS = "gradeBookSettings"
        const val GUARDIANS_ENABLED = "guardiansEnabled"
        const val ID = "id"
        const val NAME = "name"
        const val OWNER_ID = "ownerId"
        const val ROOM = "room"
        const val SECTION = "section"
        const val TEACHER_GROUP_ID = "teacherGroupId"
        const val UPDATE_TIME = "updateTime"

        // CourseWork
        const val ASSIGNEE_MODE = "assigneeMode"
        const val ASSIGNMENT = "assignment"
        const val COURSE_ID = "courseId"
        const val CREATOR_USER_ID = "creatorUserId"
        const val DUE_TIME = "dueTime"
        const val INDIVIDUAL_STUDENTS_OPTIONS = "individualStudentsOptions"
        const val MATERIALS = "materials"
        const val MAX_POINTS = "maxPoints"
        const val MULTIPLE_CHOICE_QUESTION = "multipleChoiceQuestion"
        const val SCHEDULED_TIME = "scheduledTime"
        const val STATE = "state"
        const val SUBMISSION_MODIFICATION_MODE = "submissionModificationMode"
        const val TITLE = "title"
        const val WORK_TYPE = "workType"

        // StudentSubmissions
        const val ASSIGNED_GRADE = "assignedGrade"
        const val ASSIGNMENT_SUBMISSION = "assignmentSubmission"
        const val COURSE_WORK_ID = "courseWorkId"
        const val COURSE_WORK_TYPE = "courseWorkType"
        const val LATE = "late"
        const val MULTIPLE_CHOICE_SUBMISSION = "multipleChoiceSubmission"
        const val SHORT_ANSWER_SUBMISSION = "shortAnswerSubmission"
        const val USER_ID = "userId"

        // Users
        const val CREATED_AT = "createdAt"
        const val DISPLAY_NAME = "displayName"
        const val EMAIL_ADDRESS = "emailAddress"
        const val ENROLLED = "enrolled"
        const val PHOTO_URL = "photoUrl"
        const val TEACHING = "teaching"
        const val VERIFIED = "verified"
    }

    object Storage {
        const val COURSE_STORAGE_PATH = "courses"
    }

    object Hosting {
        const val EDUMATEAPP = "https://edumateapp.web.app"
    }
}