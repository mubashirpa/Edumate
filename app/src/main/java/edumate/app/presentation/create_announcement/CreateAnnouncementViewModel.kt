package edumate.app.presentation.create_announcement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.domain.model.announcements.Link
import edumate.app.domain.model.announcements.Material
import edumate.app.domain.usecase.announcements.CreateAnnouncement
import javax.inject.Inject

@HiltViewModel
class CreateAnnouncementViewModel @Inject constructor(
    private val createAnnouncementUseCase: CreateAnnouncement
) : ViewModel() {

    var uiState by mutableStateOf(CreateAnnouncementUiState())
        private set

    fun onEvent(event: CreateAnnouncementUiEvent) {
        when (event) {
            is CreateAnnouncementUiEvent.OnAddLinkAttachment -> {
                val link = Link(
                    url = event.link,
                    title = event.link
                )
                uiState.attachments.add(Material(link = link))
            }

            is CreateAnnouncementUiEvent.OnFilePicked -> {}
            is CreateAnnouncementUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is CreateAnnouncementUiEvent.OnOpenAttachmentMenuChange -> {
                uiState = uiState.copy(openAttachmentMenu = event.open)
            }

            is CreateAnnouncementUiEvent.OnRemoveAttachment -> {
                uiState.attachments.removeAt(event.position)
            }

            is CreateAnnouncementUiEvent.OnTextChange -> {
                uiState = uiState.copy(text = event.text)
            }

            CreateAnnouncementUiEvent.PostAnnouncement -> {}
        }
    }
}