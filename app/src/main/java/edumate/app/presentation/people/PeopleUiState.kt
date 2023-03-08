package edumate.app.presentation.people

import edumate.app.domain.model.User

data class PeopleUiState(
    val showAll: Boolean = true,
    val showStudents: Boolean = false,
    val showTeachers: Boolean = false,
    val students: List<User> = emptyList(),
    val teachers: List<User> = emptyList()
)