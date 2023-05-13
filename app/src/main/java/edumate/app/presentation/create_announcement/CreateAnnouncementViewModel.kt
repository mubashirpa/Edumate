package edumate.app.presentation.create_announcement

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.AssigneeMode
import edumate.app.domain.model.DriveFile
import edumate.app.domain.model.Link
import edumate.app.domain.model.Material
import edumate.app.domain.model.announcements.Announcement
import edumate.app.domain.model.announcements.AnnouncementState
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.announcements.CreateAnnouncement
import edumate.app.domain.usecase.announcements.GetAnnouncement
import edumate.app.domain.usecase.announcements.PatchAnnouncement
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateAnnouncementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAnnouncementUseCase: CreateAnnouncement,
    private val getAnnouncementUseCase: GetAnnouncement,
    private val patchAnnouncementUseCase: PatchAnnouncement,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val validateTextField: ValidateTextField
) : ViewModel() {

    var uiState by mutableStateOf(CreateAnnouncementUiState())
        private set

    private val resultChannel = Channel<String>()
    val createAnnouncementResults = resultChannel.receiveAsFlow()

    private val courseId: String =
        checkNotNull(savedStateHandle[Routes.Args.CREATE_ANNOUNCEMENT_COURSE_ID])
    private val announcementId: String =
        checkNotNull(savedStateHandle[Routes.Args.CREATE_ANNOUNCEMENT_ID])
    private val announcement = mutableStateOf(Announcement())
    private var getUrlMetadataJob: Job? = null

    init {
        val id = generateAnnouncementId()

        announcement.value = announcement.value.copy(
            courseId = courseId,
            id = id,
            state = AnnouncementState.PUBLISHED,
            alternateLink = "${FirebaseConstants.Hosting.EDUMATEAPP}/c/p/details?cid=$courseId&aid=$id",
            assigneeMode = AssigneeMode.ALL_STUDENTS
        )

        getCurrentUserUseCase().map { user ->
            if (user != null) {
                announcement.value = announcement.value.copy(
                    creatorUserId = user.uid,
                    creatorProfile = UserProfile(
                        displayName = user.displayName,
                        emailAddress = user.email,
                        id = user.uid,
                        photoUrl = user.photoUrl?.toString(),
                        verified = user.isEmailVerified
                    )
                )
            }
        }.launchIn(viewModelScope)

        if (announcementId != "null") {
            fetchAnnouncement()
        }
    }

    fun onEvent(event: CreateAnnouncementUiEvent) {
        when (event) {
            is CreateAnnouncementUiEvent.OnAddLinkAttachment -> {
                val link = Link(
                    url = event.link,
                    title = event.link
                )
                uiState.attachments.add(Material(link = link))
                fetchUrlMetadata(event.link)
            }

            is CreateAnnouncementUiEvent.OnFilePicked -> {
                uploadFile(event.uri, event.fileUtils)
            }

            is CreateAnnouncementUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is CreateAnnouncementUiEvent.OnOpenAttachmentMenuChange -> {
                uiState = uiState.copy(openAttachmentMenu = event.open)
            }

            is CreateAnnouncementUiEvent.OnRemoveAttachment -> {
                val attachment = uiState.attachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        deleteFile(event.position)
                    }

                    attachment.link != null -> {
                        // Stop urlUseCaseJob to avoid conflict
                        getUrlMetadataJob?.cancel()
                        uiState.attachments.removeAt(event.position)
                    }
                }
            }

            is CreateAnnouncementUiEvent.OnTextChange -> {
                uiState = uiState.copy(
                    text = event.text,
                    textError = null
                )
            }

            CreateAnnouncementUiEvent.PostAnnouncement -> {
                if (announcementId != "null") {
                    updateAnnouncement()
                } else {
                    postAnnouncement()
                }
            }

            CreateAnnouncementUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun postAnnouncement() {
        val text = uiState.text
        val textResult = validateTextField.execute(text)

        if (!textResult.successful) {
            uiState = uiState.copy(textError = UiText.StringResource(Strings.missing_message))
            return
        }

        announcement.value = announcement.value.copy(
            text = text,
            materials = uiState.attachments
        )

        createAnnouncementUseCase(courseId, announcement.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val announcementResponse = resource.data
                    if (announcementResponse != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(announcementResponse.id)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchAnnouncement() {
        getAnnouncementUseCase(courseId, announcementId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(loading = true)
                }

                is Resource.Success -> {
                    val announcementResponse = resource.data
                    if (announcementResponse != null) {
                        announcement.value = announcementResponse
                        uiState.attachments.addAll(announcementResponse.materials)

                        uiState = uiState.copy(
                            loading = false,
                            text = announcementResponse.text
                        )
                    } else {
                        uiState = uiState.copy(
                            loading = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        loading = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateAnnouncement() {
        val text = uiState.text
        val textResult = validateTextField.execute(text)

        if (!textResult.successful) {
            uiState = uiState.copy(textError = UiText.StringResource(Strings.missing_message))
            return
        }

        announcement.value = announcement.value.copy(
            text = text,
            materials = uiState.attachments
        )

        patchAnnouncementUseCase(
            courseId,
            announcement.value.id,
            announcement.value
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val announcementResponse = resource.data
                    if (announcementResponse != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(announcementResponse.id)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun uploadFile(uri: Uri, fileUtils: FileUtils) {
        val fileExtension = fileUtils.getFileExtension(uri)
        val fileName = fileUtils.getFileName(uri) ?: "${uri.lastPathSegment}.$fileExtension"
        val mimeType = fileUtils.getMimeType(uri)
        val filePath =
            "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/announcements/${announcement.value.id}/$fileName"

        uploadFileUseCase(uri, filePath).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val fileUrl = resource.data
                    uiState = if (fileUrl != null) {
                        val driveFile = DriveFile(
                            url = fileUrl.toString(),
                            title = fileName,
                            type = mimeType
                        )
                        uiState.attachments.add(Material(driveFile = driveFile))
                        uiState.copy(openProgressDialog = false)
                    } else {
                        uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    uiState = uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(position: Int) {
        val fileName = uiState.attachments[position].driveFile?.title
        val filePath =
            "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/announcements/${announcement.value.id}/$fileName"

        deleteFileUseCase(filePath).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    // Stop urlUseCaseJob to avoid problems
                    getUrlMetadataJob?.cancel()
                    uiState.attachments.removeAt(position)
                    uiState.copy(openProgressDialog = false)
                }

                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = resource.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchUrlMetadata(url: String) {
        val position = uiState.attachments.lastIndex
        getUrlMetadataJob = getUrlMetadataUseCase(url).onEach { resource ->
            if (resource is Resource.Success) {
                uiState.attachments[position] = Material(link = resource.data)
            }
        }.launchIn(viewModelScope)
    }

    private fun generateAnnouncementId(): String {
        return FirebaseDatabase.getInstance()
            .getReference(FirebaseConstants.Database.ANNOUNCEMENTS_PATH)
            .child(courseId).push().key.orEmpty()
    }
}