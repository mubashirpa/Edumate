package edumate.app.presentation.join_class.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.components.TextAvatar
import edumate.app.presentation.join_class.JoinClassUiEvent
import edumate.app.presentation.join_class.JoinClassViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun JoinClassScreen(
    viewModel: JoinClassViewModel = hiltViewModel(),
    navigateToClassDetails: (courseId: String) -> Unit,
    navigateToProfile: () -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentUser = viewModel.uiState.currentUser
    val classCodeError = viewModel.uiState.classCodeError

    LaunchedEffect(context) {
        viewModel.joinClassResults.collect { courseId ->
            navigateToClassDetails(courseId)
        }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(JoinClassUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = Strings.title_join_class_screen))
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = Strings.navigate_up)
                        )
                    }
                },
                actions = {
                    UserAvatar(
                        photoUrl = currentUser?.photoUrl,
                        user = currentUser,
                        onClick = navigateToProfile
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = viewModel.uiState.classCode,
                    onValueChange = {
                        viewModel.onEvent(JoinClassUiEvent.ClassCodeChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = {
                        Text(text = stringResource(id = Strings.class_code))
                    },
                    supportingText = if (classCodeError != null) {
                        {
                            Text(
                                text = stringResource(
                                    id = Strings.ask_your_teacher_for_the_class_code
                                )
                            )
                        }
                    } else {
                        {
                            Text(
                                text = stringResource(
                                    id = Strings.class_code_shared_by_your_teacher
                                )
                            )
                        }
                    },
                    isError = classCodeError != null,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        viewModel.onEvent(JoinClassUiEvent.OnJoinClick)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = Strings.join))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    ProgressDialog(
        text = stringResource(id = Strings.joining_class),
        openDialog = viewModel.uiState.openProgressDialog
    )
}

@Composable
private fun UserAvatar(
    photoUrl: Uri?,
    user: FirebaseUser? = null,
    onClick: () -> Unit
) {
    val avatar: @Composable () -> Unit = {
        TextAvatar(
            id = user?.uid.orEmpty(),
            firstName = user?.displayName ?: user?.email.orEmpty(),
            lastName = "",
            modifier = Modifier.clickable(onClick = onClick),
            size = 30.dp
        )
    }

    Box(
        modifier = Modifier.minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClick),
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
}