package com.studbudd.application_tracker.feature_applications.domain.use_cases

import android.util.Log
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.workers.PeriodicNotificationWorker
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import java.util.concurrent.TimeUnit

class HandlePeriodicNotification(
    private val workManager: WorkManager
) {

    private fun testingWorkRequest(duration: Long, inputData: Data): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            PeriodicNotificationWorker::class.java,
            15L,
            TimeUnit.MINUTES
        ).setInitialDelay(10L, TimeUnit.SECONDS)
            .setInputData(inputData)
            .build()
    }
    operator fun invoke(jobApplication: JobApplication): Resource<Unit> {
        val uniqueWorkName = "jobApplicationNotification${jobApplication.createdAt}"

        if (shouldDeleteNotificationWork(jobApplication.status.id)) {
            workManager.cancelUniqueWork(uniqueWorkName)
            Log.d("NotificationHandler", "deleting $uniqueWorkName")
        } else {
            val inputData = getInputData(jobApplication)
            val duration = getDuration(jobApplication.status.id)
            // val work = getPeriodicWorkRequest(duration = duration, inputData = inputData)
            val work = testingWorkRequest(duration, inputData)

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                work
            )
        }

        return Resource.Success(Unit)
    }

    private fun shouldDeleteNotificationWork(status: Long): Boolean {
        return listOf(
            APPLICATION_STATUS_REJECTED,
            APPLICATION_STATUS_SELECTED
        ).contains(status)
    }

    private fun getInputData(jobApplication: JobApplication): Data {
        return Data.Builder()
            .putLong(PeriodicNotificationWorker.JOB_APPLICATION_ID, jobApplication.id)
            .putLong(PeriodicNotificationWorker.JOB_APPLICATION_STATUS, jobApplication.status.id)
            .putString(
                PeriodicNotificationWorker.JOB_APPLICATION_COMPANY,
                jobApplication.job.company
            )
            .putString(PeriodicNotificationWorker.JOB_APPLICATION_ROLE, jobApplication.job.role)
            .build()
    }

    private fun getDuration(status: Long): Long {
        return if (status == ApplicationStatus.WAITING_FOR_REFERRAL)
            REPEAT_INTERVAL_STATUS_0
        else
            REPEAT_INTERVAL_STATUS_OTHER
    }

    private fun getPeriodicWorkRequest(duration: Long, inputData: Data): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            PeriodicNotificationWorker::class.java,
            duration,
            TimeUnit.HOURS
        )
            .setInputData(inputData)
            .setInitialDelay(duration, TimeUnit.HOURS)
            .build()
    }

    companion object {
        private const val REPEAT_INTERVAL_STATUS_0 = 6L // 6hours
        private const val REPEAT_INTERVAL_STATUS_OTHER = 24 * 10L // 10days

        private const val APPLICATION_STATUS_REJECTED = 3L;
        private const val APPLICATION_STATUS_SELECTED = 4L;
    }
}