package com.studbudd.application_tracker.feature_applications.data.workers

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.studbudd.application_tracker.BaseApplication
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus

class PeriodicNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val pendingIntent = getPendingIntentForNotification()
        val notification = getNotification(pendingIntent)
        sendNotification(notification)
        return Result.success()
    }

    private fun getPendingIntentForNotification(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(JOB_APPLICATION_ID, inputData.getLong(JOB_APPLICATION_ID, -1L))
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            applicationContext,
            MainActivity.NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getNotification(
        pendingIntent: PendingIntent? = null
    ): Notification {
        val companyName = inputData.getString(JOB_APPLICATION_COMPANY)
        val jobRole = inputData.getString(JOB_APPLICATION_ROLE)
        val notificationContent: Pair<String, String> =
            when (inputData.getLong(JOB_APPLICATION_STATUS, 0L)) {
                ApplicationStatus.WAITING_FOR_REFERRAL -> Pair(
                    "Got referred?",
                    "Did you receive any referral for $companyName's $jobRole role? Be careful not to miss on the opportunity."
                )
                else -> Pair(
                    "Any updates?",
                    "How is it going with your application at $companyName for $jobRole role?"
                )
            }

        return NotificationCompat.Builder(
            applicationContext,
            BaseApplication.PERIODIC_NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(notificationContent.first)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent.second))
            .setContentText(notificationContent.second)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun sendNotification(notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(applicationContext)) {
                notify(
                    BaseApplication.PERIODIC_NOTIFICATION_ID,
                    notification
                )
            }
        }
    }

    companion object {
        const val JOB_APPLICATION_ID = "job_application_id"
        const val JOB_APPLICATION_COMPANY = "job_application_company"
        const val JOB_APPLICATION_ROLE = "job_application_role"
        const val JOB_APPLICATION_STATUS = "job_application_status"
    }

}