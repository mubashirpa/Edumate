package app.edumate.presentation.stream

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.Supabase
import app.edumate.core.UiText
import app.edumate.domain.model.announcement.Announcement
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
import app.edumate.domain.usecase.comment.UpdateCommentUseCase
import app.edumate.domain.usecase.storage.DeleteFileUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import app.edumate.presentation.components.CommentsBottomSheetUiEvent
import app.edumate.presentation.components.CommentsBottomSheetUiState
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
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(StreamUiState())
        private set
    var commentsUiState by mutableStateOf(CommentsBottomSheetUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.Stream>()
    private val courseId = args.courseId
    private var announcementId = UUID.randomUUID().toString()
    private var getAnnouncementsJob: Job? = null
    private var getAnnouncementCommentsJob: Job? = null

    init {
        getCurrentUser()
        getAnnouncements(false)
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            is StreamUiEvent.AddLinkAttachment -> {
                getUrlMetadata(event.link)
            }

            StreamUiEvent.CreateAnnouncement -> {
                val text =
                    uiState.text.text
                        .toString()
                        .trim()

                if (uiState.editAnnouncement == null) {
                    createAnnouncement(
                        text = text,
                        materials = uiState.attachments,
                    )
                } else {
                    updateAnnouncement(
                        text = text,
                        materials = uiState.attachments,
                    )
                }
            }

            is StreamUiEvent.DeleteAnnouncement -> {
                deleteAnnouncement(event.announcementId)
            }

            is StreamUiEvent.OnEditAnnouncement -> {
                val announcement = event.announcement

                if (announcement != null) {
                    if (uiState.editAnnouncement == null && uiState.attachments.isNotEmpty()) {
                        deleteFiles(uiState.attachments) {
                            updateEditAnnouncementState(announcement)
                        }
                    } else {
                        updateEditAnnouncementState(announcement)
                    }
                } else {
                    uiState = uiState.copy(editAnnouncement = null)
                    resetAnnouncementState()
                }
            }

            is StreamUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is StreamUiEvent.OnFilePicked -> {
                uploadFile(
                    title = event.title,
                    file = event.file,
                    mimeType = event.mimeType,
                    size = event.size,
                )
            }

            is StreamUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is StreamUiEvent.OnOpenDeleteAnnouncementDialogChange -> {
                uiState = uiState.copy(deleteAnnouncementId = event.announcementId)
            }

            is StreamUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
            }

            is StreamUiEvent.OnShowCommentsBottomSheetChange -> {
                val announcementId = event.announcementId
                if (announcementId != null) {
                    uiState = uiState.copy(replyAnnouncementId = event.announcementId)
                    getComments(commentsAnnouncementId = announcementId, isRefreshing = false)
                } else {
                    uiState = uiState.copy(replyAnnouncementId = null)
                    // Reset the bottom sheet state on close
                    commentsUiState = CommentsBottomSheetUiState()
                }
            }

            StreamUiEvent.Refresh -> {
                getAnnouncements(uiState.announcementResult is Result.Success)
            }

            is StreamUiEvent.RemoveAttachment -> {
                val attachment = uiState.attachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        deleteFiles(materials = listOf(attachment))
                    }

                    attachment.link != null -> {
                        uiState.attachments.removeAt(event.position)
                    }
                }
            }

            StreamUiEvent.Retry -> {
                getAnnouncements(false)
            }

            is StreamUiEvent.SetAnnouncementPinned -> {
                updateAnnouncementPinned(event.announcementId, event.pinned)
            }

            StreamUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    fun onEvent(event: CommentsBottomSheetUiEvent) {
        when (event) {
            is CommentsBottomSheetUiEvent.AddComment -> {
                uiState.replyAnnouncementId?.let { replyAnnouncementId ->
                    if (commentsUiState.editCommentId != null) {
                        updateComment(
                            commentsAnnouncementId = replyAnnouncementId,
                            commentId = commentsUiState.editCommentId!!,
                            text = event.text,
                        )
                    } else {
                        addComment(
                            commentsAnnouncementId = replyAnnouncementId,
                            text = event.text,
                        )
                    }
                }
            }

            is CommentsBottomSheetUiEvent.DeleteComment -> {
                uiState.replyAnnouncementId?.let { replyAnnouncementId ->
                    deleteComment(
                        commentsAnnouncementId = replyAnnouncementId,
                        commentId = event.commentId,
                    )
                }
            }

            is CommentsBottomSheetUiEvent.OnEditComment -> {
                commentsUiState =
                    commentsUiState.copy(editCommentId = event.commentId)
                commentsUiState.comment.setTextAndPlaceCursorAtEnd(event.text)
            }

            is CommentsBottomSheetUiEvent.OnOpenDeleteCommentDialogChange -> {
                commentsUiState =
                    commentsUiState.copy(deleteCommentId = event.commentId)
            }

            CommentsBottomSheetUiEvent.Retry -> {
                uiState.replyAnnouncementId?.let { replyAnnouncementId ->
                    getComments(
                        commentsAnnouncementId = replyAnnouncementId,
                        isRefreshing = false,
                    )
                }
            }

            CommentsBottomSheetUiEvent.UserMessageShown -> {
                commentsUiState = commentsUiState.copy(userMessage = null)
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
        text: String,
        materials: List<Material>?,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            uiState =
                uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
            return
        }

        createAnnouncementUseCase(courseId, text, materials, announcementId)
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
                        getAnnouncements(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getAnnouncements(isRefreshing: Boolean) {
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
                            val announcements = result.data!!
                            uiState =
                                uiState.copy(
                                    announcementResult = result,
                                    announcements = announcements.sortedByDescending { it.pinned },
                                    isRefreshing = false,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updateAnnouncement(
        text: String,
        materials: List<Material>,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            uiState =
                uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
            return
        }

        updateAnnouncementUseCase(announcementId, text, materials)
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
                                editAnnouncement = null,
                                openProgressDialog = false,
                            )
                        getAnnouncements(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun updateAnnouncementPinned(
        pinnedAnnouncementId: String,
        pinned: Boolean,
    ) {
        updateAnnouncementUseCase(id = pinnedAnnouncementId, pinned = pinned)
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
                        uiState = uiState.copy(openProgressDialog = false)
                        getAnnouncements(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteAnnouncement(deleteAnnouncementId: String) {
        deleteAnnouncementUseCase(deleteAnnouncementId)
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
                        getAnnouncements(true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun addComment(
        commentsAnnouncementId: String,
        text: String,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsUiState =
                commentsUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        createAnnouncementCommentUseCase(courseId, commentsAnnouncementId, text)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        commentsUiState.comment.clearText()
                        getComments(commentsAnnouncementId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getComments(
        commentsAnnouncementId: String,
        isRefreshing: Boolean,
    ) {
        // Cancel any ongoing getAnnouncementCommentsJob before making a new call.
        getAnnouncementCommentsJob?.cancel()
        getAnnouncementCommentsJob =
            getAnnouncementCommentsUseCase(commentsAnnouncementId)
                .onEach { result ->
                    when (result) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            // The Result.Error state is only used during initial loading and retry attempts.
                            // Otherwise, a snackbar is displayed using the userMessage property.
                            commentsUiState =
                                if (isRefreshing) {
                                    commentsUiState.copy(
                                        isLoading = false,
                                        userMessage = result.message,
                                    )
                                } else {
                                    commentsUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Loading -> {
                            // The Result.Loading state is only used during initial loading and retry attempts.
                            // In other cases, the PullRefreshIndicator is shown with isRefreshing = true.
                            commentsUiState =
                                if (isRefreshing) {
                                    commentsUiState.copy(isLoading = true)
                                } else {
                                    commentsUiState.copy(commentsResult = result)
                                }
                        }

                        is Result.Success -> {
                            commentsUiState =
                                commentsUiState.copy(
                                    isLoading = false,
                                    commentsResult = result,
                                )
                        }
                    }
                }.launchIn(viewModelScope)
    }

    private fun updateComment(
        commentsAnnouncementId: String,
        commentId: String,
        text: String,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            commentsUiState =
                commentsUiState.copy(
                    userMessage = UiText.StringResource(R.string.missing_message),
                )
            return
        }

        updateCommentUseCase(commentId, text)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(isLoading = true)
                    }

                    is Result.Success -> {
                        commentsUiState.comment.clearText()
                        commentsUiState =
                            commentsUiState.copy(editCommentId = null)
                        getComments(commentsAnnouncementId, true)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun deleteComment(
        commentsAnnouncementId: String,
        commentId: String,
    ) {
        deleteCommentUseCase(commentId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        commentsUiState =
                            commentsUiState.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                    }

                    is Result.Loading -> {
                        commentsUiState =
                            commentsUiState.copy(
                                deleteCommentId = null,
                                isLoading = true,
                            )
                    }

                    is Result.Success -> {
                        getComments(commentsAnnouncementId, true)
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
        title: String,
        file: File,
        mimeType: String?,
        size: Long?,
    ) {
        uploadFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            path = "$courseId/announcement/$announcementId/$title",
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
                                mimeType = mimeType,
                                size = size,
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

    private fun deleteFiles(
        materials: List<Material>,
        onSuccess: () -> Unit = {},
    ) {
        val paths =
            materials.mapNotNull { material ->
                material.driveFile?.title?.takeIf { it.isNotEmpty() }?.let {
                    "$courseId/announcement/$announcementId/$it"
                }
            }

        if (paths.isEmpty()) {
            onSuccess()
            return
        }

        deleteFileUseCase(
            bucketId = Supabase.Storage.MATERIALS_BUCKET_ID,
            paths = paths,
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
                    uiState.attachments.removeAll { material ->
                        material.driveFile?.title?.isNotEmpty() == true
                    }
                    onSuccess()
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

    private fun updateEditAnnouncementState(announcement: Announcement) {
        announcementId = announcement.id!!
        uiState =
            uiState.copy(
                attachments = announcement.materials.orEmpty().toMutableStateList(),
                editAnnouncement = announcement,
                text = uiState.text.apply { setTextAndPlaceCursorAtEnd(announcement.text.orEmpty()) },
            )
    }
}
