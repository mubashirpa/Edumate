package edumate.app.presentation.stream.screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import edumate.app.domain.model.announcements.Announcement
import edumate.app.presentation.components.TextAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsListItem(
    announcement: Announcement
) {
    OutlinedCard(
        onClick = { /*TODO*/ },
        border = BorderStroke(width = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
    ) {
        ListItem(
            headlineContent = {
                Text(text = "Mubashir P A")
            },
            supportingContent = {
                Text(text = "20 Apr")
            },
            leadingContent = {
                TextAvatar(
                    id = "8714318638",
                    firstName = "Mubashir",
                    lastName = "P A"
                )
            },
            trailingContent = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = announcement.text,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            )
            if (announcement.materials.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    announcement.materials.forEachIndexed { index, material ->
                        AssistChip(
                            onClick = { /*TODO*/ },
                            label = { Text("Attachment $index") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.InsertDriveFile,
                                    contentDescription = null
                                )
                            }
                        )
                        if (index != 8) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}