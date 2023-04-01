package edumate.app.presentation.class_details.screen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior?,
    onNavigationClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = Strings.navigate_up)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}