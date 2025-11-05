package com.example.lab_week_08.worker

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.Context
import com.example.lab_week_08.NotificationService

class NotificationLauncherWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val intent = Intent(applicationContext, NotificationService::class.java).apply {
            putExtra(NotificationService.EXTRA_ID, "001")
        }
        ContextCompat.startForegroundService(applicationContext, intent)

        Thread.sleep(4000L)
        return Result.success()
    }
}
