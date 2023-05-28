package com.studbudd.application_tracker.feature_applications.ui.draft_message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.core.domain.usecases.placeholder.PlaceholderUseCases
import com.studbudd.application_tracker.feature_applications.domain.usecases.draftmessage.DraftMessageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun getDraftMessage() = draftMessageUseCases.get()

    private val dataMap = mutableMapOf<String, String>()
    fun saveApplicationData(_dataMap: MutableMap<String, String>) {
        dataMap.putAll(_dataMap)
    }

    private val placeholderDataMap = MutableLiveData(mapOf<String, String>())
    private fun fetchPlaceholderData() = viewModelScope.launch {
        placeholderUseCases.get().collect {
            if (it is Resource.Success) {
                placeholderDataMap.postValue(it.data!!)
            }
        }
    }

    fun getPreviewMessage(message: String): String {
        return draftMessageUseCases.parse(
            message = message,
            applicationSpecificDataMap = dataMap,
            placeholderDataMap = placeholderDataMap.value ?: mapOf()
        ).data ?: message
    }

}