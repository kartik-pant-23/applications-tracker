package com.studbudd.application_tracker.view_models

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationWithStatus
import com.studbudd.application_tracker.feature_applications.data.repo.ApplicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    application: Application,
    private val repository: ApplicationsRepository
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application.applicationContext)

    private val _editMode: MutableLiveData<Boolean> = MutableLiveData()
    val editMode: LiveData<Boolean> = _editMode
    fun changeEditMode(isEditMode: Boolean) {
        _editMode.postValue(isEditMode)
    }

    // Get details of a particular application
    fun getApplication(id: Long): LiveData<JobApplicationWithStatus?> = repository.getApplication(id.toInt()).asLiveData()

    // Update Application
    fun updateApplication(newJobApplication: JobApplicationEntity_Old) = viewModelScope.launch {
        repository.updateApplication(newJobApplication)
        createNotification(newJobApplication)
    }

    // Delete Application
    fun deleteApplication(jobApplication: JobApplicationEntity_Old) = viewModelScope.launch {
        workManager.cancelUniqueWork("application${jobApplication.id}")
        repository.deleteApplication(jobApplication)
    }

    private fun createNotification(jobApplication: JobApplicationEntity_Old) {
        if (jobApplication.status > 2) {
            workManager.cancelUniqueWork("application${jobApplication.id}")
        } else {
//            val contentTitle: String = when (jobApplication.status) {
//                0 -> "Still waiting?"
//                1 -> "Are there any updates?"
//                else -> "More expectations, more excitement!!"
//            }
//            val contentText: String = when (jobApplication.status) {
//                0 -> "Did you get a referral for ${jobApplication.role} role at ${jobApplication.companyName}?"
//                1 -> "Any updates on your application at ${jobApplication.companyName} for role of ${jobApplication.role}?"
//                else -> "You applied with a referral at ${jobApplication.companyName}, ${jobApplication.role} role. Any good news?"
//            }
//            val duration: Long = when (jobApplication.status) {
//                0 -> 6
//                1 -> 7
//                else -> 15
//            }
//            val unit: TimeUnit = when (jobApplication.status) {
//                0 -> TimeUnit.HOURS
//                else -> TimeUnit.DAYS
//            }
//
//            val inputData = Data.Builder()
//                .putInt(NotifyWorker.applicationIdKey, jobApplication.id)
//                .putString(NotifyWorker.contentTitleKey, contentTitle)
//                .putString(NotifyWorker.contentTextKey, contentText)
//                .build()
//
//            val workRequest = PeriodicWorkRequest.Builder(
//                NotifyWorker::class.java,
//                duration,
//                unit
//            )
//                .setInitialDelay(duration, unit)
//                .setInputData(inputData)
//                .build()
//            workManager.enqueueUniquePeriodicWork(
//                "application${jobApplication.id}",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                workRequest
//            )
        }
    }

}