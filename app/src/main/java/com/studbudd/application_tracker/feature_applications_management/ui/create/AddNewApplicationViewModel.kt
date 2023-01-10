package com.studbudd.application_tracker.feature_applications_management.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddNewApplicationViewModel : ViewModel() {

    fun addNewApplication(
        company: String,
        role: String,
        jobLink: String,
        notes: String?,
        status: Int
    ) = viewModelScope.launch {

    }

}