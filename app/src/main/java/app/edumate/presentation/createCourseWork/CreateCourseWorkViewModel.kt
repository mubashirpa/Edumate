package app.edumate.presentation.createCourseWork

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.edumate.core.Result
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.DriveFile
import app.edumate.domain.model.material.Link
import app.edumate.domain.model.material.Material
import app.edumate.domain.usecase.GetUrlMetadataUseCase
import app.edumate.domain.usecase.storage.UploadFileUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

class CreateCourseWorkViewModel(
    private val uploadFileUseCase: UploadFileUseCase,
    private val getUrlMetadataUseCase: GetUrlMetadataUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(CreateCourseWorkUiState())
        private set

    fun onEvent(event: CreateCourseWorkUiEvent) {
        when (event) {
            CreateCourseWorkUiEvent.CreateCourseWork -> {}

            is CreateCourseWorkUiEvent.OnAddLinkAttachment -> {
                getUrlMetadata(event.link)
            }

            is CreateCourseWorkUiEvent.OnDueTimeValueChange -> {
                uiState = uiState.copy(dueTime = event.dateTime)
            }

            is CreateCourseWorkUiEvent.OnFilePicked -> {
                uploadFile(event.title, event.file)
            }

            is CreateCourseWorkUiEvent.OnOpenAddLinkDialogChange -> {
                uiState = uiState.copy(openAddLinkDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenDatePickerDialogChange -> {
                uiState = uiState.copy(openDatePickerDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenPointsDialogChange -> {
                uiState = uiState.copy(openPointsDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnOpenTimePickerDialogChange -> {
                uiState = uiState.copy(openTimePickerDialog = event.open)
            }

            is CreateCourseWorkUiEvent.OnPointsValueChange -> {
                uiState = uiState.copy(points = event.points)
            }

            is CreateCourseWorkUiEvent.OnQuestionTypeDropdownExpandedChange -> {
                uiState = uiState.copy(questionTypeDropdownExpanded = event.expanded)
            }

            is CreateCourseWorkUiEvent.OnQuestionTypeValueChange -> {
                val questionType =
                    when (event.selectionOptionIndex) {
                        0 -> CourseWorkType.SHORT_ANSWER_QUESTION
                        else -> CourseWorkType.MULTIPLE_CHOICE_QUESTION
                    }

                // Empty choices when change workType
                uiState =
                    uiState.copy(
                        choices = mutableStateListOf("Option 1"),
                        questionTypeDropdownExpanded = false,
                        questionTypeSelectionOptionIndex = event.selectionOptionIndex,
                        workType = questionType,
                    )
            }

            is CreateCourseWorkUiEvent.OnRemoveAttachment -> {
                val attachment = uiState.attachments[event.position]
                when {
                    attachment.driveFile != null -> {
                        // TODO: Delete file from drive
                    }

                    attachment.link != null -> {
                        uiState.attachments.removeAt(event.position)
                    }
                }
            }

            is CreateCourseWorkUiEvent.OnShowAddAttachmentBottomSheetChange -> {
                uiState = uiState.copy(showAddAttachmentBottomSheet = event.show)
            }

            CreateCourseWorkUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun uploadFile(
        title: String,
        file: File,
    ) {
        viewModelScope.launch {
            uploadFileUseCase("materials", "coursework/$title", file)
                .collect { result ->
                    if (result.isDone) {
                        val driveFile =
                            DriveFile(
                                alternateLink = result.url,
                                title = title,
                            )
                        uiState.attachments.add(Material(driveFile = driveFile))
                    }
                }
        }
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
                        Log.d("hello", link.toString())
                        uiState.attachments.add(Material(link = link))
                        uiState = uiState.copy(openProgressDialog = false)
                    }
                }
            }.launchIn(viewModelScope)
    }
}
