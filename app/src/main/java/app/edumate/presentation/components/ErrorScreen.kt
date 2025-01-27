package app.edumate.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.edumate.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = R.string.error_unexpected),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    Box(
        modifier = modifier.padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ErrorScreen(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = R.string.error_unknown),
    buttonText: String = stringResource(id = R.string.retry),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetryClick) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun AnimatedErrorScreen(
    url: String,
    modifier: Modifier = Modifier,
    errorMessage: String = stringResource(id = R.string.error_unexpected),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val composition = rememberLottieComposition(LottieCompositionSpec.Url(url))

    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            composition = composition.value,
            modifier = Modifier.size(200.dp),
            iterations = LottieConstants.IterateForever,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
        )
    }
}
