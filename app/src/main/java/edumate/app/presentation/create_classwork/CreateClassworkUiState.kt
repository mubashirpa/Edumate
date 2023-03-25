package edumate.app.presentation.create_classwork

import java.util.Date

data class CreateClassworkUiState(
    val attachments: List<String> = listOf(),
    val description: String = "",
    val dueDate: Date? = null,
    val openAddLinkDialog: Boolean = false,
    val openAttachmentMenu: Boolean = false,
    val openDatePickerDialog: Boolean = false,
    val openPointsDialog: Boolean = false,
    val openTimePickerDialog: Boolean = false,
    val points: String? = "100",
    val title: String = ""
)