package com.studbudd.application_tracker.feature_applications.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.use_cases.ApplicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewApplicationViewModel @Inject constructor(
    private val useCase: ApplicationsUseCase
) : ViewModel() {

    fun addNewApplication(
        company: String,
        role: String,
        jobLink: String,
        notes: String?,
        status: Int
    ) = viewModelScope.launch {

        when (useCase.create(
            company = company,
            role = role,
            url = jobLink,
            notes = notes,
            status = status
        )) {
            is Resource.Success -> println("application added")
            else -> println("application not added")
        }

    }

}