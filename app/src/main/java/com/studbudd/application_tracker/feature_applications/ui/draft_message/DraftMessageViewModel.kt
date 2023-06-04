package com.studbudd.application_tracker.feature_applications.ui.draft_message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.domain.usecases.placeholder.PlaceholderUseCases
import com.studbudd.application_tracker.feature_applications.domain.usecases.draftmessage.DraftMessageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftMessageViewModel @Inject constructor(
    private val draftMessageUseCases: DraftMessageUseCases,
    private val placeholderUseCases: PlaceholderUseCases
) : ViewModel() {

    init {
        fetchPlaceholderData()
    }

    private val dataMap = mutableMapOf<String, String>()
    fun saveApplicationData(_dataMap: MutableMap<String, String>) {
        dataMap.putAll(_dataMap)
    }

    fun getDraftMessage() = draftMessageUseCases.get()
    private val _previewMessage = MutableLiveData("")
    val previewMessage: LiveData<String> = _previewMessage
    fun getPreviewMessage(message: String) = viewModelScope.launch {
        val defaultPreview = getPreviewMessageWithPlaceholderData(message, emptyMap())
        placeholderUseCases.get()
            .catch { _previewMessage.postValue(defaultPreview) }
            .collect {
                if (it is Resource.Success)
                    _previewMessage.postValue(
                        getPreviewMessageWithPlaceholderData(
                            message,
                            it.data!!
                        )
                    )
            }
    }

    private fun getPreviewMessageWithPlaceholderData(
        message: String,
        placeholderData: Map<String, String>
    ): String {
        val res = draftMessageUseCases.parse(
            message = message,
            applicationSpecificDataMap = dataMap,
            placeholderDataMap = placeholderData
        )
        return res.data ?: res.message
    }


    private val _placeholderDataMap = MutableLiveData(mapOf<String, String>())
    val placeholderDataMap: LiveData<Map<String, String>> = _placeholderDataMap
    private fun fetchPlaceholderData() = viewModelScope.launch {
        placeholderUseCases.get().catch { }.collect {
            if (it is Resource.Success) {
                _placeholderDataMap.postValue(it.data!!)
            }
        }
    }

    fun updatePlaceholderData(updatedPlaceholderData: Map<String, String>) = viewModelScope.launch {
        val res = placeholderUseCases.update(updatedPlaceholderData)
        showMessage(res.message)
    }

    private val _uiState = MutableLiveData<DraftMessageUiState>(DraftMessageUiState.ModePreview())
    val uiState: LiveData<DraftMessageUiState> = _uiState
    fun switchToPreviewMode() = _uiState.postValue(DraftMessageUiState.ModePreview())
    fun switchToEditMode() = _uiState.postValue(DraftMessageUiState.ModeEdit())

    private fun showMessage(message: String) {
        _uiState.postValue(
            when (_uiState.value) {
                is DraftMessageUiState.ModeEdit -> DraftMessageUiState.ModeEdit(message)
                else -> DraftMessageUiState.ModePreview(message)
            }
        )
    }

}