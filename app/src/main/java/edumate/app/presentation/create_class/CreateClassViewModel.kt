package edumate.app.presentation.create_class

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import edumate.app.R.string as Strings
import edumate.app.core.FirebaseConstants
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.usecase.authentication.GetCurrentUserUseCase
import edumate.app.domain.usecase.courses.CreateCourse
import edumate.app.domain.usecase.courses.DeleteCourse
import edumate.app.domain.usecase.courses.GetCourse
import edumate.app.domain.usecase.courses.UpdateCourse
import edumate.app.domain.usecase.validation.ValidateTextField
import edumate.app.navigation.Routes
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class CreateClassViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createCourseUseCase: CreateCourse,
    private val updateCourseUseCase: UpdateCourse,
    private val getCourseUseCase: GetCourse,
    private val deleteCourseUseCase: DeleteCourse,
    private val validateTextField: ValidateTextField
) : ViewModel() {

    var uiState by mutableStateOf(CreateClassUiState())
        private set

    private val resultChannel = Channel<String>()
    val createClassResults = resultChannel.receiveAsFlow()

    private val courseId: String? = try {
        checkNotNull(savedStateHandle[Routes.Args.CREATE_CLASS_COURSE_ID])
    } catch (e: IllegalStateException) {
        null
    }
    val course = mutableStateOf(Course())

    init {
        val id = generateCourseId()

        course.value = course.value.copy(
            id = id,
            courseState = CourseState.ACTIVE,
            alternateLink = "${FirebaseConstants.Hosting.EDUMATEAPP}/c/details?cid=$id"
        )

        getCurrentUserUseCase().map { user ->
            if (user != null) {
                course.value = course.value.copy(
                    ownerId = user.uid,
                    teacherGroupId = arrayListOf(user.uid),
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

        if (courseId != null) {
            fetchCourse()
        }
    }

    fun onEvent(event: CreateClassUiEvent) {
        when (event) {
            is CreateClassUiEvent.NameChanged -> {
                uiState = uiState.copy(
                    name = event.name,
                    nameError = null
                )
            }

            is CreateClassUiEvent.RoomChanged -> {
                uiState = uiState.copy(room = event.room)
            }

            is CreateClassUiEvent.SectionChanged -> {
                uiState = uiState.copy(section = event.section)
            }

            is CreateClassUiEvent.SubjectChanged -> {
                uiState = uiState.copy(subject = event.subject)
            }

            CreateClassUiEvent.OnCreateClick -> {
                if (courseId == null) {
                    createCourse()
                } else {
                    updateCourse()
                }
            }

            CreateClassUiEvent.UserMessageShown -> {
                uiState = uiState.copy(userMessage = null)
            }
        }
    }

    private fun createCourse() {
        val name = uiState.name
        val nameResult = validateTextField.execute(name)

        if (!nameResult.successful) {
            uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_a_class_name))
            return
        }

        course.value = course.value.copy(
            name = name,
            section = uiState.section.ifEmpty { null },
            descriptionHeading = uiState.subject.ifEmpty { null },
            room = uiState.room.ifEmpty { null }
        )

        createCourseUseCase(course.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val updatedCourse = resource.data
                    if (updatedCourse != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(updatedCourse.id)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = UiText.StringResource(Strings.error_unexpected)
                        )
                    }
                }

                is Resource.Error -> {
                    val course = resource.data
                    val message = resource.message
                    if (course != null) {
                        deleteCourse(course.id, message)
                    } else {
                        uiState = uiState.copy(
                            openProgressDialog = false,
                            userMessage = message
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteCourse(id: String, message: UiText?) {
        deleteCourseUseCase(id).onEach { resource ->
            uiState = when (resource) {
                is Resource.Loading -> {
                    uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = message
                    )
                }

                is Resource.Error -> {
                    uiState.copy(
                        openProgressDialog = false,
                        userMessage = message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchCourse() {
        getCourseUseCase(courseId!!).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(loading = true)
                }

                is Resource.Success -> {
                    val updatedCourse = resource.data
                    if (updatedCourse != null) {
                        course.value = updatedCourse
                        uiState = uiState.copy(
                            loading = false,
                            name = updatedCourse.name,
                            room = updatedCourse.room.orEmpty(),
                            section = updatedCourse.section.orEmpty(),
                            subject = updatedCourse.descriptionHeading.orEmpty()
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

    private fun updateCourse() {
        val name = uiState.name
        val nameResult = validateTextField.execute(name)

        if (!nameResult.successful) {
            uiState = uiState.copy(nameError = UiText.StringResource(Strings.enter_a_class_name))
            return
        }

        course.value = course.value.copy(
            name = name,
            section = uiState.section.ifEmpty { null },
            descriptionHeading = uiState.subject.ifEmpty { null },
            room = uiState.room.ifEmpty { null }
        )

        updateCourseUseCase(course.value.id, course.value).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    uiState = uiState.copy(openProgressDialog = true)
                }

                is Resource.Success -> {
                    val id = resource.data
                    if (id != null) {
                        uiState = uiState.copy(openProgressDialog = false)
                        resultChannel.send(id)
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

    private fun generateCourseId(): String {
        return FirebaseFirestore.getInstance()
            .collection(FirebaseConstants.Firestore.COURSES_COLLECTION).document().id
    }
}