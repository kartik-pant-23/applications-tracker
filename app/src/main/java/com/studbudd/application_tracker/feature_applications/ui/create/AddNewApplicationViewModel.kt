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
            is Resource.Success -> _uiState.postValue(AddNewApplicationUiState.Success())
            else -> _uiState.postValue(AddNewApplicationUiState.Info(res.message))
        }

    }

    fun requestNotificationPermission() {
        _uiState.postValue(AddNewApplicationUiState.RequestNotificationPermission())
    }

    private fun loadListOfApplicationStatus() {
        _applicationStatus.postValue(
            listOf(
                ApplicationStatus(
                    id = 0,
                    _tag = "waiting for referral",
                    _color = "#D0A200",
                    _colorNight = "#FFC700",
                    importanceValue = 50
                ),
                ApplicationStatus(
                    id = 1,
                    _tag = "applied",
                    _color = "#006CBA",
                    _colorNight = "#44B0FF",
                    importanceValue = 40
                ),
                ApplicationStatus(
                    id = 2,
                    _tag = "applied with referral",
                    _color = "#C100AE",
                    _colorNight = "#FF8CF4",
                    importanceValue = 30
                ),
                ApplicationStatus(
                    id = 3,
                    _tag = "rejected",
                    _color = "#C90000",
                    _colorNight = "#FF5A5A",
                    importanceValue = 20
                ),
                ApplicationStatus(
                    id = 4,
                    _tag = "selected",
                    _color = "#00A811",
                    _colorNight = "#49FF5B",
                    importanceValue = 10
                )
            )
        )
    }

}