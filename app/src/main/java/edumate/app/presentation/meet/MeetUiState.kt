package edumate.app.presentation.meet

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.meetings.Meeting

data class MeetUiState(
    val appBarMenuExpanded: Boolean = false,
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val isCurrentUserTeacher: Boolean = false,
    val launchMeeting: Meeting? = null,
    val meetings: List<Meeting> = emptyList(),
    val onCreate: Boolean = false,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)