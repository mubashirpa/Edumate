package edumate.app.domain.model.student_submission

enum class SubmissionState {
    SUBMISSION_STATE_UNSPECIFIED,
    NEW,
    CREATED,
    TURNED_IN,
    RETURNED,
    RECLAIMED_BY_STUDENT
}