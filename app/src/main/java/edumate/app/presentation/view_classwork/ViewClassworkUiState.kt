package edumate.app.presentation.view_classwork

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submission.Attachment
import edumate.app.domain.model.student_submission.StudentSubmission

data class ViewClassworkUiState(
    val appBarMenuExpanded: Boolean = false,
    val classwork: CourseWork = CourseWork(),
    val dataState: DataState = DataState.UNKNOWN,
    val openProgressDialog: Boolean = false,
    val openRemoveAttachmentDialog: Int? = null,
    val openTurnInDialog: Boolean = false,
    val openUnSubmitDialog: Boolean = false,
    val openYourWorkBottomSheet: Boolean = false,
    val refreshing: Boolean = false,
    val studentSubmission: StudentSubmission? = null,
    val studentSubmissionAttachments: SnapshotStateList<Attachment> = mutableStateListOf(),
    val userMessage: UiText? = null,
    val yourWorkDataState: DataState = DataState.UNKNOWN
)