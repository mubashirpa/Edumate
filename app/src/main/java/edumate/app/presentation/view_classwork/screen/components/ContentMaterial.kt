package edumate.app.presentation.view_classwork.screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.core.ext.header
import edumate.app.presentation.view_classwork.ViewClassworkUiEvent
import edumate.app.presentation.view_classwork.ViewClassworkUiState
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentMaterial(
    uiState: ViewClassworkUiState,
    onEvent: (ViewClassworkUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomMargin = navigationBarHeight + 10.dp
    val contentPadding = PaddingValues(
        start = 16.dp,
        top = 10.dp,
        end = 16.dp,
        bottom = bottomMargin
    )
    val refreshState = rememberPullRefreshState(
        refreshing = uiState.refreshing,
        onRefresh = { onEvent(ViewClassworkUiEvent.OnRefresh) }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                header {
                    Text(
                        text = uiState.classwork.title,
                        modifier = Modifier.padding(top = 6.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                val description = uiState.classwork.description
                if (description != null) {
                    header {
                        Text(
                            text = description,
                            modifier = Modifier.padding(top = 6.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                val attachments = uiState.classwork.materials
                if (attachments.isNotEmpty()) {
                    header {
                        Text(
                            text = stringResource(id = Strings.attachments),
                            modifier = Modifier.padding(
                                top = 14.dp,
                                bottom = 6.dp
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(attachments) {
                        AttachmentsListItem(
                            attachment = it,
                            onClick = { url ->
                                if (url != null) {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(browserIntent)
                                }
                            }
                        )
                    }
                }
            }
        )

        PullRefreshIndicator(
            uiState.refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}