package edumate.app.presentation.view_classwork

import android.net.Uri
import edumate.app.core.utils.FileUtils

sealed class ViewClassworkUiEvent {
    data class OnFilePicked(val uri: Uri, val fileUtils: FileUtils) : ViewClassworkUiEvent()
    data class OnOpenYourWorkBottomSheet(val open: Boolean) : ViewClassworkUiEvent()
    data class OnRemoveAttachment(val index: Int) : ViewClassworkUiEvent()
    object TurnIn : ViewClassworkUiEvent()
    object UnSubmit : ViewClassworkUiEvent()
}