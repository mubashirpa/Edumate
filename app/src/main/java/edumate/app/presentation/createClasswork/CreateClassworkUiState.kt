package edumate.app.presentation.createClasswork

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.classroom.courseWork.CourseWorkType
import java.util.Calendar

data class CreateClassworkUiState(
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val choices: SnapshotStateList<String> = mutableStateListOf("Option 1"),
    val description: String = "",
    val dueDate: Calendar? = null,
    val isLoading: Boolean = false,
    val openAddLinkDialog: Boolean = false,
    val openDatePickerDialog: Boolean = false,
    val openPointsDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val openTimePickerDialog: Boolean = false,
    val points: String? = "100",
    val questionTypeDropdownExpanded: Boolean = false,
    val questionTypeSelectionOption: String = "",
    val showAddAttachmentBottomSheet: Boolean = false,
    val title: String = "",
    val titleError: UiText? = null,
    val userId: String = "",
    val userMessage: UiText? = null,
    val workType: CourseWorkType? = null,
)
