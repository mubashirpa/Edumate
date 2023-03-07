package edumate.app.presentation.people

import edumate.app.domain.model.User

data class PeopleUiState(
    val students: List<User> = emptyList(),
    val teachers: List<User> = emptyList(),
    val showAll: Boolean = true,
    val showTeachers: Boolean = false,
    val showStudents: Boolean = false
)