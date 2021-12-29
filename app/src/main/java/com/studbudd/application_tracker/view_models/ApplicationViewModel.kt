package com.studbudd.application_tracker.view_models

import androidx.lifecycle.*
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.data.ApplicationsRepository
import kotlinx.coroutines.launch

class ApplicationViewModel (private val repository: ApplicationsRepository) : ViewModel() {

    val applicationsList: LiveData<List<Application>> = repository.applicationsList.asLiveData()

    fun getApplication(id: Int): LiveData<Application> = repository.getApplication(id).asLiveData()

    fun insertApplication(application: Application) = viewModelScope.launch {
        repository.insertApplication(application)
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