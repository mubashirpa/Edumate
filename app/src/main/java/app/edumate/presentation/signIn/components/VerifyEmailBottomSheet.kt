package app.edumate.presentation.signIn.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailBottomSheet(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onResendVerifyEmail: () -> Unit,
) {
    if (show) {
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = bottomSheetState,
        ) {
            VerifyEmailBottomSheetContent(
                onResendVerifyEmail = {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onDismissRequest()
                            onResendVerifyEmail()
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun VerifyEmailBottomSheetContent(
    onResendVerifyEmail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = Icons.Default.PersonOutline, contentDescription = null)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.verify_your_account),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.please_check_your_email_for_a_verification_link),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onResendVerifyEmail,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.resend_verification_email))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VerifyEmailBottomSheetPreview() {
    EdumateTheme {
        VerifyEmailBottomSheetContent(onResendVerifyEmail = {})
    }
}
