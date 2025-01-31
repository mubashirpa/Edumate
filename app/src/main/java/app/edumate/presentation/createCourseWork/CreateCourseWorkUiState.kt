package app.edumate.presentation.createCourseWork

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.edumate.core.UiText
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.Material
import kotlinx.datetime.LocalDateTime

data class CreateCourseWorkUiState(
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val choices: SnapshotStateList<String> = mutableStateListOf("Option 1"),
    val description: TextFieldState = TextFieldState(),
    val dueTime: LocalDateTime? = null,
    val isCreateCourseWorkSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val openAddLinkDialog: Boolean = false,
    val openDatePickerDialog: Boolean = false,
    val openPointsDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val openTimePickerDialog: Boolean = false,
    val points: String? = "100",
    val questionTypeDropdownExpanded: Boolean = false,
    val questionTypeSelectionOptionIndex: Int? = null,
    val showAddAttachmentBottomSheet: Boolean = false,
    val title: TextFieldState = TextFieldState(),
    val titleError: UiText? = null,
    val uploadProgress: Float? = null,
    val userMessage: UiText? = null,
    val workType: CourseWorkType? = null,
)
