package edumate.app.presentation.view_classwork

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.student_submission.Attachment

data class ViewClassworkUiState(
    val attachments: SnapshotStateList<Attachment> = mutableStateListOf(),
    val classwork: CourseWork = CourseWork(),
    val dataState: DataState = DataState.UNKNOWN,
    val openProgressDialog: Boolean = false,
    val openYourWorkBottomSheet: Boolean = false,
    val userMessage: UiText? = null
)