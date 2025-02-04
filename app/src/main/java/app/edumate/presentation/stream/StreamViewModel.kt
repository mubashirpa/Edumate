package app.edumate.presentation.stream

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.Supabase
import app.edumate.core.UiText
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material
import app.edumate.domain.usecase.GetUrlMetadataUseCase
import app.edumate.domain.usecase.announcement.CreateAnnouncementCommentUseCase
import app.edumate.domain.usecase.announcement.CreateAnnouncementUseCase
import app.edumate.domain.usecase.announcement.DeleteAnnouncementUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementCommentsUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementsUseCase
import app.edumate.domain.usecase.announcement.UpdateAnnouncementUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.comment.DeleteCommentUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.util.UUID

class StreamViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAnnouncementUseCase: CreateAnnouncementUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val createAnnouncementCommentUseCase: CreateAnnouncementCommentUseCase,
    private val getAnnouncementCommentsUseCase: GetAnnouncementCommentsUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(StreamUiState())
        private set
    var commentsBottomSheetUiState by mutableStateOf(CommentsBottomSheetUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.Stream>()
    private var getAnnouncementsJob: Job? = null
    private var getAnnouncementCommentsJob: Job? = null
    private var announcementId = UUID.randomUUID().toString()

    init {
        getCurrentUser()
        getAnnouncements(
            courseId = args.courseId,
            isRefreshing = false,
        )
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            is StreamUiEvent.AddComment -> {
                addComment(
                    announcementId = event.announcementId,
                    text = event.text,
                )
            }

            is StreamUiEvent.AddLinkAttachment -> {
                getUrlMetadata(event.link)
            }

            StreamUiEvent.CreateAnnouncement -> {
                val text =
                    uiState.text.text
                        .toString()
                        .trim()

                if (uiState.editAnnouncementId == null) {
                    createAnnouncement(
                        courseId = args.courseId,
                        id = announcementId,
                        text = text,
                        materials = uiState.attachments,
                    )
                } else {
                    updateAnnouncement(
                        courseId = args.courseId,
                        id = announcementId,
                        text = text,
                        materials = uiState.attachments,
                    )
                }
            }

            is StreamUiEvent.DeleteAnnouncement -> {
                deleteAnnouncement(event.id)
            }

            is StreamUiEvent.DeleteComment -> {
                deleteComment(
                    announcementId = event.announcementId,
                    id = event.id,
                )
            }

            is StreamUiEvent.OnEditAnnouncement -> {
                val announcement = event.announcement
                uiState = uiState.copy(editAnnouncementId = announcement?.id)

                if (announcement != null) {
                    announcementId = announcement.id.orEmpty()
                    uiState.text.setTextAndPlaceCursorAtEnd(announcement.text.orEmpty())
                    uiState.attachments.clear()
                    uiState.attachments.addAll(announcement.materials.orEmpty())
                } else {
                    resetAnnouncementState()
                }
            }

            is StreamUiEvent.OnEditComment -> {
                // TODO
            }

            is StreamUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is StreamUiEvent.OnFilePicked -> {
                uploadFile(
                    courseId = args.courseId,
                    id = announcementId,
                    title = event.title,
                    file = event.file,
                )
            }

            is StreamUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is StreamUiEvent.OnOpenDeleteAnnouncementDialogChange -> {
                uiState = uiState.copy(deleteAnnouncementId = event.announcementId)
            }

            is StreamUiEvent.OnOpenDeleteCommentDialogChange -> {
                commentsBottomSheetUiState =
                    commentsBottomSheetUiState.copy(deleteCommentId = event.commentId)
            }

            is StreamUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
            }

            is StreamUiEvent.OnShowCommentsBottomSheetChange -> {
                val announcementId = event.announcementId
                if (announcementId != null) {
                    uiState = uiState.copy(replyAnnouncementId = event.announcementId)
                    getComments(announcementId = announcementId, isRefreshing = false)
                } else {
                    uiState = uiState.copy(replyAnnouncementId = null)
                    // Reset the bottom sheet state on close
                    commentsBottomSheetUiState = CommentsBottomSheetUiState()
                }
            }

            StreamUiEvent.Refresh -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = true,
                )
            }

            is StreamUiEvent.RemoveAttachment -> {
                val attachment = uiState.attachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        deleteFile(
                            courseId = args.courseId,
                            id = announcementId,
                            material = attachment,
                        )
                    }

                    attachment.link != null -> {
                        uiState.attachments.removeAt(event.position)
                    }
                }
            }

            StreamUiEvent.Retry -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = false,
                )
            }

            is StreamUiEvent.RetryComment -> {
                getComments(
                    announcementId = event.announcementId,
                    isRefreshing = false,
                )
            }

            StreamUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
                commentsBottomSheetUiState = commentsBottomSheetUiState.copy(userMessage = null)
            }
        }
    }

    private fun getCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                if (result is Result.Success) {
                    result.data?.id?.let { userId ->
                        uiState = uiState.copy(currentUserId = userId)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun createAnnouncement(
        courseId: String,
        id: String,
        text: String,
        materials: List<Material>?,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            uiState =
                uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
            return
        }

        createAnnouncementUseCase(courseId, text, materials, id)
            .onEach { result ->
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
                        resetAnnouncementState()
                        uiState = uiState.copy(openProgressDialog = false)
                        getAnnouncements(courseId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getAnnouncements(
        courseId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getAnnouncementsJob before making a new call.
        getAnnouncementsJob?.cancel()
        getAnnouncementsJob =
            getAnnouncementsUseCase(courseId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(
                                        isRefreshing = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    uiState.copy(announcementResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            uiState =
                                if (isRefreshing) {
                                    uiState.copy(isRefreshing = true)
                                } else {
                                    uiState.copy(announcementResult = result)
                                }
                        }

                        is Result.Success -> {
                            uiState =
                                uiState.copy(
                                    isRefreshing = false,
                                    announcementResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updateAnnouncement(
        courseId: String,
        id: String,
        text: String,
        materials: List<Material>?,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            uiState =
                uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
            return
        }

        updateAnnouncementUseCase(id, text, materials)
            .onEach { result ->
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
                        resetAnnouncementState()
                        uiState =
                            uiState.copy(
                                editAnnouncementId = null,
                                openProgressDialog = false,
                            )
                        getAnnouncements(courseId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteAnnouncement(id: String) {
        deleteAnnouncementUseCase(id)
            .onEach { result ->
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
                        uiState =
                            uiState.copy(
                                deleteAnnouncementId = null,
                                openProgressDialog = true,
                            )
                    }

                    is Result.Success -> {
                        uiState = uiState.copy(openProgressDialog = false)
                        getAnnouncements(args.courseId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun addComment(
        announcementId: String,
        text: String,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsBottomSheetUiState =
                commentsBottomSheetUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        createAnnouncementCommentUseCase(announcementId, text)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsBottomSheetUiState =
                            commentsBottomSheetUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsBottomSheetUiState =
                            commentsBottomSheetUiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        commentsBottomSheetUiState.comment.clearText()
                        getComments(announcementId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getComments(
        announcementId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getAnnouncementCommentsJob before making a new call.
        getAnnouncementCommentsJob?.cancel()
        getAnnouncementCommentsJob =
            getAnnouncementCommentsUseCase(announcementId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            commentsBottomSheetUiState =
                                if (isRefreshing) {
                                    commentsBottomSheetUiState.copy(
                                        isLoading = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    commentsBottomSheetUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            commentsBottomSheetUiState =
                                if (isRefreshing) {
                                    commentsBottomSheetUiState.copy(isLoading = true)
                                } else {
                                    commentsBottomSheetUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Success -> {
                            commentsBottomSheetUiState =
                                commentsBottomSheetUiState.copy(
                                    isLoading = false,
                                    commentsResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun deleteComment(
        announcementId: String,
        id: String,
    ) {
        deleteCommentUseCase(id)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsBottomSheetUiState =
                            commentsBottomSheetUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsBottomSheetUiState =
                            commentsBottomSheetUiState.copy(
                                deleteCommentId = null,
                                isLoading = true,
                            )
                    }

                    is Result.Success -> {
                        getComments(announcementId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getUrlMetadata(url: String) {
        getUrlMetadataUseCase(url)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        val link =
                            Link(
                                url = url,
                                title = url,
                            )
                        uiState.attachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }

                    is Result.Loading -> {
                        uiState = uiState.copy(openProgressDialog = true)
                    }

                    is Result.Success -> {
                        val link = result.data!!
                        uiState.attachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun uploadFile(
        courseId: String,
        id: String,
        title: String,
        file: File,
    ) {
        uploadFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            path = "$courseId/announcement/$id/$title",
            file = file,
        ).onEach { result ->
            when (result) {
                is Result.Empty -> {}

                is Result.Error -> {
                    uiState =
                        uiState.copy(
                            uploadProgress = null,
                            userMessage = result.message,
                        )
                }

                is Result.Loading -> {
                    uiState = uiState.copy(uploadProgress = 0.0f)
                }

                is Result.Success -> {
                    val state = result.data!!
                    if (state.isDone) {
                        val driveFile =
                            DriveFile(
                                alternateLink = state.url,
                                title = title,
                            )
                        uiState.attachments.add(Material(driveFile = driveFile))
                        uiState = uiState.copy(uploadProgress = null)
                    } else {
                        uiState = uiState.copy(uploadProgress = state.progress)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteFile(
        courseId: String,
        id: String,
        material: Material,
    ) {
        val title = material.driveFile?.title ?: return

        deleteFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            paths = listOf("$courseId/announcement/$id/$title"),
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
                    uiState.attachments.remove(material)
                    uiState = uiState.copy(openProgressDialog = false)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun resetAnnouncementState() {
        announcementId = UUID.randomUUID().toString()
        uiState.text.clearText()
        uiState.attachments.clear()
    }
}
