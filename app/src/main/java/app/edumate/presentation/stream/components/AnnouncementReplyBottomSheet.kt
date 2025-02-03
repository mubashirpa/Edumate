package app.edumate.presentation.stream.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.model.member.Member
import app.edumate.domain.model.member.UserRole
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.courseDetails.CourseUserRole
import kotlin.collections.orEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementReplyBottomSheet(
    show: Boolean,
    commentsResult: Result<List<Comment>>,
    members: List<Member>,
    currentUserRole: CourseUserRole,
    currentUserId: String,
    onDismissRequest: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val isFullscreen = bottomSheetState.targetValue == SheetValue.Expanded
        val cornerSize by animateDpAsState(
            targetValue = if (isFullscreen) 0.dp else 28.dp,
            label = stringResource(id = R.string.label_animate_bottom_sheet_corner_size),
        )
        val paddingValues = WindowInsets.systemBars.asPaddingValues()

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize),
            dragHandle = {
                val topPadding by animateDpAsState(
                    targetValue = if (isFullscreen) paddingValues.calculateTopPadding() else 0.dp,
                    label = stringResource(id = R.string.label_animate_bottom_sheet_top_padding),
                )
                BottomSheetDefaults.DragHandle(modifier = Modifier.padding(top = topPadding))
            },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                when (commentsResult) {
                    is Result.Empty -> {}

                    is Result.Error -> {
                        ErrorScreen(
                            onRetryClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxSize(),
                            errorMessage = commentsResult.message!!.asString(),
                        )
                    }

                    is Result.Loading -> {
                        LoadingScreen()
                    }

                    is Result.Success -> {
                        val comments = commentsResult.data.orEmpty()

                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(bottom = 12.dp),
                            content = {
                                items(
                                    items = comments,
                                    key = { it.id!! },
                                ) { comment ->
                                    val commentUserRole =
                                        members
                                            .find {
                                                it.userId == comment.creatorUserId
                                            }?.role ?: UserRole.STUDENT

                                    ReplyListItem(
                                        comment = comment,
                                        itemUserRole = commentUserRole,
                                        currentUserRole = currentUserRole,
                                        currentUserId = currentUserId,
                                        onEditClick = { /*TODO*/ },
                                        onDeleteClick = { /*TODO*/ },
                                    )
                                }
                            },
                        )
                        HorizontalDivider(thickness = Dp.Hairline)
                        CommentTextField(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentTextField(modifier: Modifier = Modifier) {
    OutlinedTextField(
        state = rememberTextFieldState(),
        modifier = modifier,
        placeholder = {
            Text(text = stringResource(R.string.add_a_comment))
        },
        trailingIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                )
            }
        },
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = true,
                imeAction = ImeAction.Send,
            ),
        onKeyboardAction = { /*TODO*/ },
        lineLimits = TextFieldLineLimits.SingleLine,
        shape = CircleShape,
    )
}
