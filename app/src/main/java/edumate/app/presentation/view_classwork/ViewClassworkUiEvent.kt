package edumate.app.presentation.view_classwork

import android.net.Uri
import edumate.app.core.utils.FileUtils

sealed class ViewClassworkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ViewClassworkUiEvent()
    data class OnEditShortAnswerChange(val edit: Boolean) : ViewClassworkUiEvent()
    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : ViewClassworkUiEvent()
    data class OnMultipleChoiceAnswerChange(val answer: String) : ViewClassworkUiEvent()
    data class OnOpenHandInDialog(val open: Boolean) : ViewClassworkUiEvent()
    data class OnOpenRemoveAttachmentDialog(val index: Int?) : ViewClassworkUiEvent()
    data class OnOpenTurnInDialog(val open: Boolean) : ViewClassworkUiEvent()
    data class OnOpenUnSubmitDialog(val open: Boolean) : ViewClassworkUiEvent()
    data class OnOpenYourWorkBottomSheet(val open: Boolean) : ViewClassworkUiEvent()
    data class OnRemoveAttachment(val index: Int) : ViewClassworkUiEvent()
    data class OnShortAnswerChange(val answer: String) : ViewClassworkUiEvent()
    object OnRefresh : ViewClassworkUiEvent()
    object TurnIn : ViewClassworkUiEvent()
    object UnSubmit : ViewClassworkUiEvent()
    object UserMessageShown : ViewClassworkUiEvent()
}