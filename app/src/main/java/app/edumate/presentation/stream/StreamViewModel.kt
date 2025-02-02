package app.edumate.presentation.stream

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.material.Material
import app.edumate.domain.usecase.announcement.CreateAnnouncementUseCase
import app.edumate.domain.usecase.announcement.DeleteAnnouncementUseCase
import app.edumate.domain.usecase.announcement.GetAnnouncementsUseCase
import app.edumate.domain.usecase.announcement.UpdateAnnouncementUseCase
import app.edumate.domain.usecase.authentication.GetCurrentUserUseCase
import app.edumate.domain.usecase.validation.ValidateTextField
import app.edumate.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StreamViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val createAnnouncementUseCase: CreateAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val updateAnnouncementUseCase: UpdateAnnouncementUseCase,
    private val validateTextField: ValidateTextField,
) : ViewModel() {
    var uiState by mutableStateOf(StreamUiState())
        private set

    private val args = savedStateHandle.toRoute<Screen.Stream>()
    private var getAnnouncementsJob: Job? = null

    init {
        getCurrentUser()
        getAnnouncements(
            courseId = args.courseId,
            isRefreshing = false,
        )
    }

    fun onEvent(event: StreamUiEvent) {
        when (event) {
            StreamUiEvent.CreateAnnouncement -> {
                createAnnouncement(
                    courseId = args.courseId,
                    text =
                        uiState.text.text
                            .toString()
                            .trim(),
                    materials = uiState.attachments,
                )
            }

            is StreamUiEvent.OnDeleteAnnouncement -> {
                deleteAnnouncement(event.id)
            }

            is StreamUiEvent.OnExpandedAppBarDropdownChange -> {
                uiState = uiState.copy(expandedAppBarDropdown = event.expanded)
            }

            is StreamUiEvent.OnOpenDeleteAnnouncementDialogChange -> {
                uiState = uiState.copy(deleteAnnouncementId = event.announcementId)
            }

            StreamUiEvent.OnRefresh -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = true,
                )
            }

            StreamUiEvent.OnRetry -> {
                getAnnouncements(
                    courseId = args.courseId,
                    isRefreshing = false,
                )
            }

            StreamUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
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
        text: String,
        materials: List<Material>?,
    ) {
        val textResult = validateTextField.execute(text)
        if (!textResult.successful) {
            uiState = uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
            return
        }

        createAnnouncementUseCase(courseId, text, materials)
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
                        uiState.text.clearText()
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
            uiState = uiState.copy(userMessage = UiText.StringResource(R.string.missing_message))
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
                        uiState = uiState.copy(openProgressDialog = false)
                        uiState.text.clearText()
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
}
