package com.studbudd.application_tracker.feature_applications.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import com.studbudd.application_tracker.feature_applications.domain.use_cases.ApplicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val useCase: ApplicationsUseCase
): ViewModel() {

    init {
        loadApplications()
    }

    private val _applicationsList = MutableLiveData<List<JobApplication>>(emptyList())
    val applicationsList: LiveData<List<JobApplication>> = _applicationsList

    private val _uiState = MutableLiveData<ApplicationsUiState>(ApplicationsUiState.Default())
    val uiState = _uiState

    private fun loadApplications(
        pageSize: Int = 100,
        currentPage: Int = 1
    ) = viewModelScope.launch {
        useCase.get(pageSize, currentPage).collect {
            _applicationsList.postValue(it.data ?: emptyList())
            if (it is Resource.Failure) {
                _uiState.postValue(ApplicationsUiState.Info(it.message))
            } else if (it is Resource.LoggedOut) {
                _uiState.postValue(ApplicationsUiState.LoggedOut())
            }
        }
    }

}