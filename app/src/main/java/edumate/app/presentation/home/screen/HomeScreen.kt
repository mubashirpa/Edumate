package edumate.app.presentation.home.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen() {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(id = Strings.app_name))
        }, navigationIcon = {
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues)
        ) {
            LazyColumn(contentPadding = PaddingValues(10.dp), content = {
                item {
                    Card(
                        onClick = { }, modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Name",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Text(
                                        text = "Section", style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "0 students", style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            })
        }
    }
}

@Preview(
    device = "id:pixel_6_pro",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}