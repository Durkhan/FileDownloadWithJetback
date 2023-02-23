package com.vholodynskyi.filedownloadingtest.domain.use_case.workers

import android.app.NotificationManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class CancelWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.cancel(NotificationWorker.NOTIFICATION_ID)
        return Result.success()
    }
}
