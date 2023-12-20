package edumate.app.presentation.meet.screen.components

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edumate.app.domain.model.meetings.Meeting
import edumate.app.domain.model.meetings.MeetingState
import edumate.app.presentation.components.FilledIcon
import edumate.app.presentation.meet.MeetUiState
import edumate.app.R.string as Strings

@Composable
fun MeetListItem(
    meeting: Meeting,
    uiState: MeetUiState,
    onEndClick: (Meeting) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClick: () -> Unit,
) {
    val creationTime = meeting.creationTime

    ListItem(
        headlineContent = {
            Text(text = meeting.title ?: meeting.meetingId)
        },
        modifier = Modifier.clickable(onClick = onClick),
        supportingContent =
            if (creationTime != null) {
                {
                    val date =
                        DateUtils.getRelativeTimeSpanString(
                            creationTime,
                        )
                    Text(
                        text =
                            stringResource(
                                id = Strings.created_,
                                date,
                            ),
                    )
                }
            } else {
                null
            },
        leadingContent = {
            FilledIcon(imageVector = Icons.Default.Link)
        },
        trailingContent =
            if (uiState.isCurrentUserTeacher) {
                if (meeting.creatorUserId == uiState.currentUser?.uid) {
                    {
                        if (meeting.state == MeetingState.LIVE) {
                            IconButton(onClick = { onEndClick(meeting) }) {
                                Icon(imageVector = Icons.Default.StopCircle, contentDescription = null)
                            }
                        } else {
                            IconButton(onClick = { onDeleteClick(meeting.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    }
                } else {
                    null
                }
            } else {
                {
                    Text(text = stringResource(id = Strings.join))
                }
            },
    )
}
