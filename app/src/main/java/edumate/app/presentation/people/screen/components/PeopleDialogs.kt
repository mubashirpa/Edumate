package edumate.app.presentation.people.screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import edumate.app.R.string as Strings
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.TextAvatar

@Composable
fun LeaveClassDialog(
    onDismissRequest: () -> Unit,
    openDialog: Boolean,
    onConfirmClick: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = Strings.leave_class))
            },
            text = {
                Text(text = stringResource(id = Strings.leave_class_warning_message))
            },
            confirmButton = {
                TextButton(onClick = onConfirmClick) {
                    Text(stringResource(id = Strings.leave_class))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}

@Composable
fun RemoveUserDialog(
    onDismissRequest: () -> Unit,
    userProfile: UserProfile?,
    userType: UserType,
    onConfirmClick: (userType: UserType, uid: String) -> Unit
) {
    if (userProfile != null) {
        val photoUrl = userProfile.photoUrl
        val userId = userProfile.id
        val avatar: @Composable () -> Unit = {
            TextAvatar(
                id = userId,
                firstName = userProfile.displayName.orEmpty(),
                lastName = ""
            )
        }
        val title = if (userType == UserType.TEACHER) {
            stringResource(id = Strings.remove_teacher)
        } else {
            stringResource(id = Strings.remove_student)
        }

        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = title)
            },
            text = {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (photoUrl != null) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(photoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        ) {
                            when (painter.state) {
                                is AsyncImagePainter.State.Loading -> {
                                    avatar()
                                }

                                is AsyncImagePainter.State.Error -> {
                                    avatar()
                                }

                                else -> {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        }
                    } else {
                        avatar()
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = userProfile.displayName.orEmpty())
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirmClick(userType, userId) }) {
                    Text(stringResource(id = Strings.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = Strings.cancel))
                }
            }
        )
    }
}