package edumate.app.presentation.view_classwork.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings
import edumate.app.core.ext.header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewClassworkScreen(
    onBackPressed: () -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        MediumTopAppBar(
            title = {
                Text(
                    text = "Edumate",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = Strings.navigate_up)
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
        Column(modifier = Modifier.fillMaxSize()) {
            val bottomMargin = WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding() + 10.dp
            val contentPadding = PaddingValues(
                start = 10.dp,
                top = 10.dp,
                end = 10.dp,
                bottom = bottomMargin
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    header {
                        Column {
                            Text(
                                text = "Description",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Attachments",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    items(10) {
                        Card(onClick = { /*TODO*/ }) {
                            Box(modifier = Modifier.size(128.dp))
                        }
                    }
                }
            )
        }
    }
}