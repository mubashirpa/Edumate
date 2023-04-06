package edumate.app.presentation.people.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import edumate.app.domain.model.User
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.components.TextAvatar

@Composable
fun PeopleListItem(
    user: User,
    modifier: Modifier = Modifier,
    currentUserType: UserType,
    currentUserId: String,
    courseOwnerId: String,
    onLeaveClass: () -> Unit,
    onEmail: () -> Unit,
    onRemove: () -> Unit
) {
    val photoUrl = user.photoUrl
    val userId = user.id
    val avatar: @Composable () -> Unit = {
        TextAvatar(
            id = userId,
            firstName = user.displayName.orEmpty(),
            lastName = ""
        )
    }
    val trailingContent: @Composable (() -> Unit)? =
        if (currentUserType == UserType.TEACHER) { // Current user is a teacher
            val isCurrentUser = userId == currentUserId
            val isCourseOwner = userId == courseOwnerId

            if (isCurrentUser && isCourseOwner) {
                null
            } else {
                {
                    MenuButton(
                        isCurrentUser = isCurrentUser,
                        isCourseOwner = isCourseOwner,
                        onLeaveClass = onLeaveClass,
                        onEmail = onEmail,
                        onRemove = onRemove
                    )
                }
            }
        } else {
            null
        }

    ListItem(
        headlineContent = {
            Text(text = user.displayName.orEmpty())
        },
        modifier = modifier,
        leadingContent = {
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
        },
        trailingContent = trailingContent
    )
}

@Composable
private fun MenuButton(
    isCurrentUser: Boolean,
    isCourseOwner: Boolean,
    onLeaveClass: () -> Unit,
    onEmail: () -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (isCurrentUser) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.leave_class)) },
                    onClick = {
                        expanded = false
                        onLeaveClass()
                    }
                )
            } else if (isCourseOwner) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.email)) },
                    onClick = {
                        expanded = false
                        onEmail()
                    }
                )
            } else {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.email)) },
                    onClick = {
                        expanded = false
                        onEmail()
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = Strings.remove)) },
                    onClick = {
                        expanded = false
                        onRemove()
                    }
                )
            }
        }
    }
}