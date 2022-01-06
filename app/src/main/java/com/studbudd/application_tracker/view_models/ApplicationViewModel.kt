package com.studbudd.application_tracker.view_models

import android.util.Log
import androidx.lifecycle.*
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.data.ApplicationsRepository
import kotlinx.coroutines.launch

class ApplicationViewModel (private val repository: ApplicationsRepository) : ViewModel() {

    private val _applicationsList: MutableLiveData<List<Application>> = MutableLiveData()
    val applicationsList: LiveData<List<Application>> = _applicationsList

    private val _editMode: MutableLiveData<Boolean> = MutableLiveData()
    val editMode: LiveData<Boolean> = _editMode
    fun changeEditMode(isEditMode: Boolean) {
        _editMode.postValue(isEditMode)
    }

    // Adding variables to apply filters while showing applications
    private var _status: List<Int> = listOf(0,1,2,3,4)
    private var _orderByCreated: Boolean = true
    private var _latestFirst: Boolean = true

    // Getting all applications list
    fun getAllApplications() = viewModelScope.launch {
        _applicationsList.postValue(repository.getAllApplications(_orderByCreated, _latestFirst, _status))
    }

    // TODO: Apply filters before showing applications list
    // fun applyFilters() {
    // }

    // Get details of a particular application
    fun getApplication(id: Int): LiveData<Application> = repository.getApplication(id).asLiveData()

    // Insert New Application
    fun insertApplication(application: Application) = viewModelScope.launch {
        repository.insertApplication(application)
    }

    // TODO: Updating application
     fun updateApplication(newApplication: Application) = viewModelScope.launch {
         repository.updateApplication(newApplication)
     }

     fun deleteApplication(application: Application) = viewModelScope.launch {
         repository.deleteApplication(application)
     }

    class ApplicationsViewModelFactory(private val repository: ApplicationsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ApplicationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ApplicationViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}