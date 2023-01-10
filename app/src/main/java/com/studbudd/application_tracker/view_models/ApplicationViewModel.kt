package com.studbudd.application_tracker.view_models

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.studbudd.application_tracker.feature_applications_management.data.entity.JobApplication
import com.studbudd.application_tracker.feature_applications_management.data.repo.ApplicationsRepository
import com.studbudd.application_tracker.workers.NotifyWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    application: Application,
    private val repository: ApplicationsRepository
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application.applicationContext)
    private val _applicationsList: MutableLiveData<List<JobApplication>> = MutableLiveData()
    val applicationsList: LiveData<List<JobApplication>> = _applicationsList
    private val firebaseAnalytics = Firebase.analytics

    private val _editMode: MutableLiveData<Boolean> = MutableLiveData()
    val editMode: LiveData<Boolean> = _editMode
    fun changeEditMode(isEditMode: Boolean) {
        _editMode.postValue(isEditMode)
    }

    // Adding variables to apply filters while showing applications
    private var _status: List<Int> = listOf(0, 1, 2, 3, 4)
    private var _orderByCreated: Boolean = true
    private var _latestFirst: Boolean = true

    // Getting all applications list
    fun getAllApplications() = viewModelScope.launch {
        _applicationsList.postValue(
            repository.getAllApplications(
                _orderByCreated,
                _latestFirst,
                _status
            )
        )
    }

    // TODO: Apply filters before showing applications list
    // fun applyFilters() {
    // }

    // Get details of a particular application
    fun getApplication(id: Int): LiveData<JobApplication> = repository.getApplication(id).asLiveData()

    // Insert New Application
    fun insertApplication(jobApplication: JobApplication) = viewModelScope.launch {
        val id = repository.insertApplication(jobApplication)
        jobApplication.application_id = id.toInt()
        createNotification(jobApplication)

        firebaseAnalytics.logEvent("application_created") {
            param("status", jobApplication.status.toLong())
            param("has_notes", if (jobApplication.notes.isNullOrBlank()) "true" else "false")
        }
    }

    // Update Application
    fun updateApplication(newJobApplication: JobApplication) = viewModelScope.launch {
        repository.updateApplication(newJobApplication)
        createNotification(newJobApplication)
    }

    // Delete Application
    fun deleteApplication(jobApplication: JobApplication) = viewModelScope.launch {
        workManager.cancelUniqueWork("application${jobApplication.application_id}")
        repository.deleteApplication(jobApplication)
    }

    private fun createNotification(jobApplication: JobApplication) {
        if (jobApplication.status > 2) {
            workManager.cancelUniqueWork("application${jobApplication.application_id}")
        } else {
            val contentTitle: String = when (jobApplication.status) {
                0 -> "Still waiting?"
                1 -> "Are there any updates?"
                else -> "More expectations, more excitement!!"
            }
            val contentText: String = when (jobApplication.status) {
                0 -> "Did you get a referral for ${jobApplication.role} role at ${jobApplication.company_name}?"
                1 -> "Any updates on your application at ${jobApplication.company_name} for role of ${jobApplication.role}?"
                else -> "You applied with a referral at ${jobApplication.company_name}, ${jobApplication.role} role. Any good news?"
            }
            val duration: Long = when (jobApplication.status) {
                0 -> 6
                1 -> 7
                else -> 15
            }
            val unit: TimeUnit = when (jobApplication.status) {
                0 -> TimeUnit.HOURS
                else -> TimeUnit.DAYS
            }

            val inputData = Data.Builder()
                .putInt(NotifyWorker.applicationIdKey, jobApplication.application_id)
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
                .build()
            workManager.enqueueUniquePeriodicWork(
                "application${jobApplication.application_id}",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

}