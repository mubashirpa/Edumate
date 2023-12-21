package edumate.app.presentation.gemini_summarize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeminiSummarizeViewModel
    @Inject
    constructor(
        private val generativeModel: GenerativeModel,
    ) : ViewModel() {
        private val _uiState: MutableStateFlow<GeminiSummarizeUiState> =
            MutableStateFlow(GeminiSummarizeUiState.Initial)
        val uiState: StateFlow<GeminiSummarizeUiState> = _uiState.asStateFlow()

        fun summarizeStreaming(inputText: String) {
            _uiState.value = GeminiSummarizeUiState.Loading

            val prompt = "Summarize the following text for me: $inputText"

            viewModelScope.launch {
                try {
                    var outputContent = ""
                    generativeModel.generateContentStream(prompt)
                        .collect { response ->
                            outputContent += response.text
                            _uiState.value = GeminiSummarizeUiState.Success(outputContent)
                        }
                } catch (e: Exception) {
                    _uiState.value = GeminiSummarizeUiState.Error(e.localizedMessage ?: "")
                }
            }
        }
    }
