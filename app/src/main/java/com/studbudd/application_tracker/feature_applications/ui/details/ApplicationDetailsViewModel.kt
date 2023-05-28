package com.studbudd.application_tracker.feature_applications.ui.details

import androidx.lifecycle.*
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import com.studbudd.application_tracker.feature_applications.domain.usecases.ApplicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationDetailsViewModel @Inject constructor(
    private val useCase: ApplicationsUseCase
) : ViewModel() {

    init {
        fetchApplicationStatus()
    }

    private val _application = MutableLiveData<JobApplication>()
    val application: LiveData<JobApplication> = _application

    private val _availableStatus = MutableLiveData(emptyList<ApplicationStatus>())
    val availableStatus =
        _availableStatus.asFlow().combine(application.asFlow()) { statuses, data ->
            Pair(statuses, data.status)
        }

    private val _uiState = MutableLiveData<ApplicationDetailsUiState>()
    val uiState: LiveData<ApplicationDetailsUiState> = _uiState

    fun fetchApplicationDetails(id: Long) = viewModelScope.launch {
        useCase.getDetails(id).collect {
            if (it is Resource.Success) {
                _application.postValue(it.data!!)
            } else {
                _uiState.postValue(ApplicationDetailsUiState.ApplicationDeleted())
            }
        }
    }

    private fun showMessage(message: String) {
        if (_uiState.value is ApplicationDetailsUiState.EditMode) {
            _uiState.postValue(ApplicationDetailsUiState.EditMode(message))
        } else {
            _uiState.postValue(ApplicationDetailsUiState.Info(message))
        }
    }

    private fun fetchApplicationStatus() = viewModelScope.launch {
        useCase.getApplicationStatus().collect {
            if (it is Resource.Success) {
                _availableStatus.postValue(it.data!!)
            } else {
                _uiState.postValue(ApplicationDetailsUiState.ApplicationDeleted())
            }
        }
    }

    fun updateApplication(notes: String?, status: Long) = viewModelScope.launch {
        _application.value?.let {
            when (val res = useCase.update(id = it.id, notes = notes, status = status)) {
                is Resource.Success -> {
                    _uiState.postValue(ApplicationDetailsUiState.Info("Application updated successfully."))
                }
                is Resource.Failure -> {
                    _uiState.postValue(ApplicationDetailsUiState.EditMode(res.message))
                }
                is Resource.LoggedOut -> {
                    _uiState.postValue(ApplicationDetailsUiState.Info("User logged out."))
                }
            }
        }
    }

    fun deleteApplication() = viewModelScope.launch {
        application.value?.let {
            val res = useCase.delete(it.id)
            if (res is Resource.Failure) showMessage(res.message)
            else if (res is Resource.LoggedOut) println("user logged out.")
        } ?: showMessage("Oops!! Something went wrong.")
    }

    fun enableEditMode() {
        _uiState.postValue(ApplicationDetailsUiState.EditMode())
    }

    fun disableEditMode() {
        _uiState.postValue(ApplicationDetailsUiState.Default())
    }

}