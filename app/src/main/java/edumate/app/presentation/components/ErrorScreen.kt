package edumate.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import edumate.app.R.string as Strings

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = Strings.error_unexpected)
) {
    Box(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = Strings.error_unknown),
    buttonTitle: String = stringResource(id = Strings.retry),
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text(text = buttonTitle)
        }
    }
}

@Composable
fun AnimatedErrorScreen(
    url: String,
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = Strings.error_unexpected)
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))

    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition,
            modifier = Modifier.size(200.dp),
            iterations = LottieConstants.IterateForever
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center
        )
    }
}