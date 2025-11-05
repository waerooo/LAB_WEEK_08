package com.example.lab_week_08.worker

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.content.Context
import com.example.lab_week_08.SecondNotificationService

class SecondNotificationLauncherWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val intent = Intent(applicationContext, SecondNotificationService::class.java).apply {
            putExtra(SecondNotificationService.EXTRA_ID, "002")
        }
        ContextCompat.startForegroundService(applicationContext, intent)

        Thread.sleep(4000L)
        return Result.success()
    }
}
