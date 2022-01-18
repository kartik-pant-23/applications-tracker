package com.studbudd.application_tracker.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.data.ApplicationsRepository
import com.studbudd.application_tracker.workers.NotifyWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ApplicationViewModel (application: android.app.Application, private val repository: ApplicationsRepository) : ViewModel() {

    private val workManager = WorkManager.getInstance(application.applicationContext)
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
        createNotification(application)
        repository.insertApplication(application)
    }

    // Update Application
    fun updateApplication(newApplication: Application) = viewModelScope.launch {
        createNotification(newApplication)
        repository.updateApplication(newApplication)
    }

    // Delete Application
    fun deleteApplication(application: Application) = viewModelScope.launch {
        workManager.cancelUniqueWork("application${application.application_id}")
        repository.deleteApplication(application)
    }

    private fun createNotification(application: Application) {
        if (application.status > 2) {
            workManager.cancelUniqueWork("application${application.application_id}")
        } else {
            val contentTitle: String = when (application.status) {
                0 -> "Still waiting?"
                1 -> "Are there any updates?"
                else -> "More expectations, more excitement!!"
            }
            val contentText: String = when (application.status) {
                0 -> "Did you get a referral for ${application.role} role at ${application.company_name}?"
                1 -> "Any updates on your application at ${application.company_name} for role of ${application.role}?"
                else -> "You applied with a referral at ${application.company_name}, ${application.role} role. Any good news?"
            }
            val duration: Long = when (application.status) {
                0 -> 6
                1 -> 7
                else -> 15
            }
            val unit: TimeUnit = when (application.status) {
                0 -> TimeUnit.HOURS
                else -> TimeUnit.DAYS
            }

            val inputData = Data.Builder()
                .putInt(NotifyWorker.applicationIdKey, application.application_id)
                .putString(NotifyWorker.contentTitleKey, contentTitle)
                .putString(NotifyWorker.contentTextKey, contentText)
                .build()

            val workRequest = PeriodicWorkRequest.Builder(
                NotifyWorker::class.java,
                duration,
                unit
            )
                .setInitialDelay(duration, unit)
                .setInputData(inputData)
                .addTag("application${application.application_id}")
                .build()
            workManager.enqueueUniquePeriodicWork(
                "application${application.application_id}",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    class ApplicationsViewModelFactory(private val application: android.app.Application, private val repository: ApplicationsRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ApplicationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ApplicationViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}