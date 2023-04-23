package edumate.app.presentation.create_classwork

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.course_work.Material
import java.util.Date

data class CreateClassworkUiState(
    val attachments: SnapshotStateList<Material> = mutableStateListOf(),
    val choices: SnapshotStateList<String> = mutableStateListOf("Option 1"),
    val description: String = "",
    val dueDate: Date? = null,
    val loading: Boolean = false,
    val openAddLinkDialog: Boolean = false,
    val openAttachmentMenu: Boolean = false,
    val openDatePickerDialog: Boolean = false,
    val openPointsDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val openTimePickerDialog: Boolean = false,
    val points: String? = "100",
    val title: String = "",
    val titleError: UiText? = null,
    val userMessage: UiText? = null,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED
)