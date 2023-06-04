package com.studbudd.application_tracker.feature_applications.ui.draft_message

sealed class DraftMessageUiState(
    val isPreviewMode: Boolean,
    val message: String?
) {

    class ModePreview(message: String? = null): DraftMessageUiState(true, message)

    class ModeEdit(message: String? = null): DraftMessageUiState(false, message)

}