package com.studbudd.application_tracker.feature_applications.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.use_cases.ApplicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewApplicationViewModel @Inject constructor(
    private val useCase: ApplicationsUseCase
) : ViewModel() {

    private val _applicationStatus = MutableLiveData<List<ApplicationStatus>>(emptyList())
    val applicationStatus: LiveData<List<ApplicationStatus>> = _applicationStatus

    private val _uiState = MutableLiveData<AddNewApplicationUiState>(AddNewApplicationUiState.Default())
    val uiState: LiveData<AddNewApplicationUiState> = _uiState

    init {
        loadListOfApplicationStatus()
    }

    fun addNewApplication(
        company: String,
        role: String,
        jobLink: String,
        notes: String?,
        status: Long
    ) = viewModelScope.launch {

        when (val res = useCase.create(
            company = company,
            role = role,
            url = jobLink,
            notes = notes,
            status = status
        )) {
            is Resource.Success -> _uiState.postValue(AddNewApplicationUiState.Success(res.message))
            else -> _uiState.postValue(AddNewApplicationUiState.Info(res.message))
        }

    }

    private fun loadListOfApplicationStatus() = viewModelScope.launch {
        useCase.getApplicationStatus(lowerRange = 30).collect {
            if (it is Resource.Success) {
                _applicationStatus.postValue(it.data!!)
            } else {
                _uiState.postValue(AddNewApplicationUiState.Info(it.message))
            }
        }
    }

}