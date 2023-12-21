package edumate.app.presentation.gemini_summarize

sealed interface GeminiSummarizeUiState {
    /**
     * Empty state when the screen is first shown
     */
    data object Initial : GeminiSummarizeUiState

    /**
     * Still loading
     */
    data object Loading : GeminiSummarizeUiState

    /**
     * Text has been generated
     */
    data class Success(
        val outputText: String,
    ) : GeminiSummarizeUiState

    /**
     * There was an error generating text
     */
    data class Error(
        val errorMessage: String,
    ) : GeminiSummarizeUiState
}
