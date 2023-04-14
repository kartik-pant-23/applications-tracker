package com.studbudd.application_tracker.feature_applications.ui.draft_message

import androidx.lifecycle.ViewModel
import com.studbudd.application_tracker.core.domain.DraftMessageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DraftMessageViewModel @Inject constructor(
    private val draftMessageUseCases: DraftMessageUseCases
) : ViewModel() {

    private val dataMap = mutableMapOf<String, String>()

    fun getDraftMessage() = draftMessageUseCases.get()

    fun getPreviewMessage(message: String) = draftMessageUseCases.parse(message = message, dataMap)

    fun saveApplicationData(_dataMap: MutableMap<String, String>) {
        dataMap.putAll(_dataMap)
    }

}