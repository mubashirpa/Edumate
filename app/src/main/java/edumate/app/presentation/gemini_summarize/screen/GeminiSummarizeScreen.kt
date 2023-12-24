package edumate.app.presentation.gemini_summarize.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.presentation.gemini_summarize.GeminiSummarizeUiState
import edumate.app.presentation.gemini_summarize.GeminiSummarizeViewModel

@Composable
fun GeminiSummarizeScreen(viewModel: GeminiSummarizeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var textToSummarize by rememberSaveable {
        mutableStateOf("")
    }
    val errorMessage = "Text input too long"
    var isError by rememberSaveable { mutableStateOf(false) }
    val charLimit = 2000

    fun validate(text: String) {
        isError = text.length > charLimit
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
    ) {
        ElevatedCard {
            Column(modifier = Modifier.padding(10.dp)) {
                OutlinedTextField(
                    value = textToSummarize,
                    onValueChange = {
                        textToSummarize = it
                        validate(textToSummarize)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .semantics {
                                if (isError) error(errorMessage)
                            },
                    label = {
                        Text(text = "Write about")
                    },
                    placeholder = {
                        Text(text = "Tell us what you want to write about")
                    },
                    supportingText = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${textToSummarize.length}/$charLimit",
                            textAlign = TextAlign.End,
                        )
                    },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions =
                        KeyboardActions(
                            onSend = {
                                keyboardController?.hide()
                                validate(textToSummarize)
                            },
                        ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        if (textToSummarize.isNotBlank()) {
                            viewModel.summarizeStreaming(textToSummarize)
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(text = "Go")
                }
            }
        }
        when (uiState) {
            GeminiSummarizeUiState.Initial -> {
                // Nothing is shown
            }

            GeminiSummarizeUiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .padding(all = 8.dp)
                            .align(Alignment.CenterHorizontally),
                ) {
                    CircularProgressIndicator()
                }
            }

            is GeminiSummarizeUiState.Success -> {
                Text(
                    text = (uiState as GeminiSummarizeUiState.Success).outputText,
                    modifier =
                        Modifier
                            .padding(10.dp)
                            .animateContentSize(animationSpec = tween(300, easing = LinearEasing)),
                )
            }

            is GeminiSummarizeUiState.Error -> {
                Text(
                    text = (uiState as GeminiSummarizeUiState.Error).errorMessage,
                    modifier = Modifier.padding(10.dp),
                )
            }
        }
    }
}
