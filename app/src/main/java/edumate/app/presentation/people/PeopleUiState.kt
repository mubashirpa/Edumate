package edumate.app.presentation.people

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.students.Student
import edumate.app.domain.model.classroom.teachers.Teacher
import edumate.app.domain.model.userProfiles.UserProfile

data class PeopleUiState(
    val appBarDropdownExpanded: Boolean = false,
    val deleteUserProfile: UserProfile? = null,
    val filter: PeopleFilterType = PeopleFilterType.ALL,
    val isStudentsRefreshing: Boolean = false,
    val isTeachersRefreshing: Boolean = false,
    val isUserLeaveClass: Boolean = false,
    val openLeaveClassDialog: Boolean = false,
    val openProgressDialog: Boolean = false,
    val showInviteBottomSheet: Boolean = false,
    val studentsResult: Result<List<Student>> = Result.Empty(),
    val teachersResult: Result<List<Teacher>> = Result.Empty(),
    val userId: String? = null,
    val userMessage: UiText? = null,
)
