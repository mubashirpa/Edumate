package app.edumate.presentation.pdfViewer

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import app.edumate.R
import app.edumate.presentation.components.LoadingScreen
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    pdfUrl: String,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    pdfTitle: String? = null,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var expandedAppBarDropdown by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    pdfTitle?.let {
                        Text(
                            text = it,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                        IconButton(
                            onClick = {
                                expandedAppBarDropdown = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = expandedAppBarDropdown,
                            onDismissRequest = {
                                expandedAppBarDropdown = false
                            },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.open_with))
                                },
                                onClick = {
                                    expandedAppBarDropdown = false
                                    val intent = Intent(Intent.ACTION_VIEW, pdfUrl.toUri())
                                    context.startActivity(intent)
                                },
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        PdfRendererViewCompose(
            url = pdfUrl,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            statusCallBack =
                object : PdfRendererView.StatusCallBack {
                    override fun onError(error: Throwable) {
                        super.onError(error)
                        loading = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(error.message.toString())
                        }
                    }

                    override fun onPdfLoadStart() {
                        super.onPdfLoadStart()
                        loading = true
                    }

                    override fun onPdfLoadSuccess(absolutePath: String) {
                        super.onPdfLoadSuccess(absolutePath)
                        loading = false
                    }
                },
        )
        if (loading) {
            LoadingScreen()
        }
    }
}
