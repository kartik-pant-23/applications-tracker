package com.studbudd.application_tracker.feature_applications_management.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_applications_management.domain.use_cases.AddJobApplicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewApplicationViewModel @Inject constructor(
    private val addJobApplication: AddJobApplicationUseCase
) : ViewModel() {

    fun addNewApplication(
        company: String,
        role: String,
        jobLink: String,
        notes: String?,
        status: Int
    ) = viewModelScope.launch {

        when (addJobApplication(
            companyName = company,
            role = role,
            jobLink = jobLink,
            notes = notes,
            status = status
        )) {
            is Resource.Success -> println("application added")
            else -> println("application not added")
        }

    }

}