package edumate.app.presentation.stream.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edumate.app.domain.model.courses.Course

@Composable
fun CourseTitleBanner(course: Course) {
    val index = remember { (0..6).random() }
    val images = remember {
        listOf(
            "https://gstatic.com/classroom/themes/Geography_thumb.jpg",
            "https://gstatic.com/classroom/themes/Writing_thumb.jpg",
            "https://gstatic.com/classroom/themes/Math_thumb.jpg",
            "https://gstatic.com/classroom/themes/Chemistry_thumb.jpg",
            "https://gstatic.com/classroom/themes/Physics_thumb.jpg",
            "https://gstatic.com/classroom/themes/Psychology_thumb.jpg",
            "https://gstatic.com/classroom/themes/img_graduation_thumb.jpg",
            "https://gstatic.com/classroom/themes/SocialStudies_thumb.jpg"
        )
    }
    val backgroundImage = images[index % images.size]

    Card(modifier = Modifier.aspectRatio(3f / 1f)) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(backgroundImage)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = course.name,
                            color = Color.White,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    supportingContent = {
                        if (course.section != null) {
                            Text(
                                text = course.section,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}