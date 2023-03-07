package edumate.app.presentation.people

sealed class PeopleUiEvent {
    data class OnFilterChange(val peopleFilter: PeopleFilter) : PeopleUiEvent()
}