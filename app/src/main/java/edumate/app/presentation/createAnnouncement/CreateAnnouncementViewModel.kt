package edumate.app.presentation.createAnnouncement

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.core.FirebaseConstants
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.core.utils.FileUtils
import edumate.app.domain.model.classroom.DriveFile
import edumate.app.domain.model.classroom.Link
import edumate.app.domain.model.classroom.Material
import edumate.app.domain.model.classroom.announcements.Announcement
import edumate.app.domain.usecase.GetUrlMetadataUseCase
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.classroom.announcements.CreateAnnouncementUseCase
import edumate.app.domain.usecase.classroom.announcements.GetAnnouncementUseCase
import edumate.app.domain.usecase.classroom.announcements.UpdateAnnouncementUseCase
import edumate.app.domain.usecase.storage.DeleteFileUseCase
import edumate.app.domain.usecase.storage.UploadFileUseCase
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import edumate.app.R.string as Strings

@HiltViewModel
class CreateAnnouncementViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        getCurrentUserUseCase: GetCurrentUserUseCase,
        private val createAnnouncementUseCase: CreateAnnouncementUseCase,
        private val getAnnouncementUseCase: GetAnnouncementUseCase,
        private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
        private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
        private val uploadFileUseCase: UploadFileUseCase,
        private val deleteFileUseCase: DeleteFileUseCase,
        private val validateTextField: ValidateTextField,
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
            getCurrentUserUseCase().map { user ->
                user.id?.let { userId ->
                    uiState = uiState.copy(userId = userId)
                }
            }.launchIn(viewModelScope)
            if (announcementId != "null") {
                getAnnouncement(announcementId)
            }
        }

        fun onEvent(event: CreateAnnouncementUiEvent) {
            when (event) {
                is CreateAnnouncementUiEvent.OnAddLinkAttachment -> {
                    val link =
                        Link(
                            url = event.link,
                            title = event.link,
                        )
                    uiState.attachments.add(Material(link = link))
                    getUrlMetadata(event.link)
                }

                is CreateAnnouncementUiEvent.OnFilePicked -> {
                    uploadFile(event.uri, event.fileUtils)
                }

                is CreateAnnouncementUiEvent.OnOpenAddLinkDialogChange -> {
                    uiState = uiState.copy(openAddLinkDialog = event.openDialog)
                }

                is CreateAnnouncementUiEvent.OnRemoveAttachment -> {
                    val attachment = uiState.attachments[event.position]
                    when {
                        attachment.driveFile != null -> {
                            deleteFile(event.position)
                        }

                        attachment.link != null -> {
                            // Cancel getUrlMetadataJob to prevent potential conflicts.
                            // If getUrlMetadataJob is running in the background and succeeds,
                            // it may re-add the deleted data.
                            getUrlMetadataJob?.cancel()
                            uiState.attachments.removeAt(event.position)
                        }
                    }
                }

                is CreateAnnouncementUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                    uiState = uiState.copy(showAddAttachmentBottomSheet = event.showBottomSheet)
                }

                is CreateAnnouncementUiEvent.OnTextValueChange -> {
                    uiState =
                        uiState.copy(
                            text = event.text,
                            textError = null,
                        )
                }

                CreateAnnouncementUiEvent.PostAnnouncement -> {
                    if (announcementId != "null") {
                        updateAnnouncement(announcementId)
                    } else {
                        createAnnouncement()
                    }
                }

                CreateAnnouncementUiEvent.UserMessageShown -> {
                    uiState = uiState.copy(userMessage = null)
                }
            }
        }

        private fun createAnnouncement() {
            val text = uiState.text
            val textResult = validateTextField.execute(text)

            if (!textResult.successful) {
                uiState = uiState.copy(textError = UiText.StringResource(Strings.missing_message))
                return
            }

            announcement.value =
                announcement.value.copy(
                    text = text,
                    materials = uiState.attachments,
                )

            createAnnouncementUseCase(courseId, announcement.value).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val announcementResponse = result.data
                        if (announcementResponse != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(announcementResponse.id.orEmpty())
                        } else {
                            uiState =
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun getAnnouncement(id: String) {
            getAnnouncementUseCase(courseId, id).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        val announcementResponse = result.data
                        if (announcementResponse != null) {
                            announcement.value = announcementResponse
                            uiState.attachments.addAll(announcementResponse.materials.orEmpty())
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    text = announcementResponse.text.orEmpty(),
                                )
                        } else {
                            uiState =
                                uiState.copy(
                                    isLoading = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun updateAnnouncement(id: String) {
            val text = uiState.text
            val textResult = validateTextField.execute(text)

            if (!textResult.successful) {
                uiState = uiState.copy(textError = UiText.StringResource(Strings.missing_message))
                return
            }

            announcement.value =
                announcement.value.copy(
                    text = text,
                    materials = uiState.attachments,
                )

            updateAnnouncementUseCase(
                courseId,
                id,
                announcement.value,
            ).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val announcementResponse = result.data
                        if (announcementResponse != null) {
                            uiState = uiState.copy(openProgressDialog = false)
                            resultChannel.send(announcementResponse.id.orEmpty())
                        } else {
                            uiState =
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun uploadFile(
            uri: Uri,
            fileUtils: FileUtils,
        ) {
            val fileExtension = fileUtils.getFileExtension(uri)
            val fileName = fileUtils.getFileName(uri) ?: "${uri.lastPathSegment}.$fileExtension"
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/announcements/${announcement.value.id}/$fileName"

            uploadFileUseCase(uri, filePath).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val fileUrl = result.data
                        uiState =
                            if (fileUrl != null) {
                                val driveFile =
                                    DriveFile(
                                        alternateLink = fileUrl.toString(),
                                        title = fileName,
                                    )
                                uiState.attachments.add(Material(driveFile = driveFile))
                                uiState.copy(openProgressDialog = false)
                            } else {
                                uiState.copy(
                                    openProgressDialog = false,
                                    userMessage = UiText.StringResource(Strings.error_unexpected),
                                )
                            }
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun deleteFile(position: Int) {
            val fileName = uiState.attachments[position].driveFile?.title
            val filePath =
                "${FirebaseConstants.Storage.COURSE_STORAGE_PATH}/$courseId/announcements/${announcement.value.id}/$fileName"

            deleteFileUseCase(filePath).onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        uiState =
                            uiState.copy(
                                openProgressDialog = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        uiState.attachments.removeAt(position)
                        uiState = uiState.copy(openProgressDialog = false)
                    }
                }
            }.launchIn(viewModelScope)
        }

        private fun getUrlMetadata(url: String) {
            val position = uiState.attachments.lastIndex
            getUrlMetadataJob =
                getUrlMetadataUseCase(url).onEach { result ->
                    if (result is Result.Success) {
                        uiState.attachments[position] = Material(link = result.data)
                    }
                }.launchIn(viewModelScope)
        }
    }
