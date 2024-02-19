package edumate.app.presentation.getStarted

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import edumate.app.core.Constants
import edumate.app.presentation.ui.theme.EdumateTheme
import edumate.app.R.string as Strings

@Composable
fun GetStartedScreen(navigateToLogin: () -> Unit) {
    val backgroundPlaceholder = rememberAsyncImagePainter(Constants.BACKDROP_GET_STARTED_LOCAL)
    val colorStops =
        arrayOf(
            0.1f to Color.Transparent,
            1f to Color.Black,
        )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(contentAlignment = Alignment.TopCenter) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(Constants.BACKDROP_GET_STARTED)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                placeholder = backgroundPlaceholder,
                error = backgroundPlaceholder,
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colorStops = colorStops)),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = Strings.app_name).lowercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = Strings.get_started_message),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = navigateToLogin,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = stringResource(id = Strings.get_started))
                }
            }
        }
    }
}

@Preview
@Composable
private fun GetStartedScreenPreview() {
    EdumateTheme(dynamicColor = false) {
        GetStartedScreen(navigateToLogin = {})
    }
}
