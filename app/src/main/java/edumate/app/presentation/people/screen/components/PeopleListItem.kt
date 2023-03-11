package edumate.app.presentation.people.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import edumate.app.domain.model.User
import edumate.app.presentation.components.TextAvatar

@Composable
fun PeopleListItem(user: User) {
    val photoUrl = user.photoUrl
    val avatar: @Composable () -> Unit = {
        TextAvatar(
            id = user.emailAddress.orEmpty(),
            firstName = user.displayName.orEmpty(),
            lastName = ""
        )
    }

    ListItem(
        headlineContent = {
            Text(text = user.displayName.orEmpty())
        },
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
        }
    )
}