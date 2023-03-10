package com.studbudd.application_tracker.core.data.workers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.studbudd.application_tracker.BaseApplication
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R

class NotifyWorker(
    context: Context,
    workerParams: WorkerParameters
): Worker(context, workerParams) {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun doWork(): Result {
        val applicationId = inputData.getInt(applicationIdKey, -1)
        val contentText = inputData.getString(contentTextKey)
        val contentTitle = inputData.getString(contentTitleKey)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        intent.putExtra(applicationIdKey, applicationId)
        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, BaseApplication.CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(0, notification)
        }

        return Result.success()
    }

    companion object {
        const val applicationIdKey: String = "application_id"
        const val contentTitleKey: String = "content_title"
        const val contentTextKey: String = "content_text"
    }

}