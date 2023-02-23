package com.vholodynskyi.filedownloadingtest.domain.use_case.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(androidx.loader.R.drawable.notification_bg)
            .setContentTitle("Download completed")
            .setContentText("Your file has been downloaded successfully!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()


        notificationManager.notify(NOTIFICATION_ID, notification)



        return Result.success()
    }

    companion object {
        const val TAG = "NotificationWorker"
        const val CHANNEL_ID = "DOWNLOAD_CHANNEL"
        const val CHANNEL_NAME = "Download Notifications"
        const val NOTIFICATION_ID = 1
    }
}
