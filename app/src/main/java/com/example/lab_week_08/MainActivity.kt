package com.example.lab_week_08

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.*
import com.example.lab_week_08.worker.FirstWorker
import com.example.lab_week_08.worker.SecondWorker
import com.example.lab_week_08.worker.ThirdWorker
import com.example.lab_week_08.worker.NotificationLauncherWorker
import com.example.lab_week_08.worker.SecondNotificationLauncherWorker

class MainActivity : AppCompatActivity() {

    private val workManager by lazy { WorkManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Request notification permission (Android 13+) — tetapkan jika perlu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val id = "001"

        val firstRequest = OneTimeWorkRequest.Builder(FirstWorker::class.java)
            .setConstraints(constraints)
            .setInputData(getIdInputData(FirstWorker.INPUT_DATA_ID, id))
            .build()

        val secondRequest = OneTimeWorkRequest.Builder(SecondWorker::class.java)
            .setConstraints(constraints)
            .setInputData(getIdInputData(SecondWorker.INPUT_DATA_ID, id))
            .build()

        // Launcher worker yang akan start NotificationService
        val notifyLauncherRequest = OneTimeWorkRequest.Builder(NotificationLauncherWorker::class.java)
            .setConstraints(constraints)
            .build()

        val thirdRequest = OneTimeWorkRequest.Builder(ThirdWorker::class.java)
            .setConstraints(constraints)
            .setInputData(getIdInputData(ThirdWorker.INPUT_DATA_ID, id))
            .build()

        // Launcher worker yang akan start SecondNotificationService
        val secondNotifyLauncherRequest = OneTimeWorkRequest.Builder(SecondNotificationLauncherWorker::class.java)
            .setConstraints(constraints)
            .build()

        // Chain: First -> Second -> NotificationLauncher -> Third -> SecondNotificationLauncher
        workManager.beginWith(firstRequest)
            .then(secondRequest)
            .then(notifyLauncherRequest)
            .then(thirdRequest)
            .then(secondNotifyLauncherRequest)
            .enqueue()

        // Observers hanya untuk menampilkan toast ketika tiap worker selesai
        workManager.getWorkInfoByIdLiveData(firstRequest.id).observe(this) { info ->
            if (info.state.isFinished) showResult("First process is done")
        }

        workManager.getWorkInfoByIdLiveData(secondRequest.id).observe(this) { info ->
            if (info.state.isFinished) showResult("Second process is done")
        }

        workManager.getWorkInfoByIdLiveData(notifyLauncherRequest.id).observe(this) { info ->
            if (info.state.isFinished) showResult("NotificationService (via launcher) done")
        }

        workManager.getWorkInfoByIdLiveData(thirdRequest.id).observe(this) { info ->
            if (info.state.isFinished) showResult("Third process is done")
        }

        workManager.getWorkInfoByIdLiveData(secondNotifyLauncherRequest.id).observe(this) { info ->
            if (info.state.isFinished) showResult("SecondNotificationService (via launcher) done")
        }
    }

    private fun getIdInputData(idKey: String, idValue: String) =
        Data.Builder().putString(idKey, idValue).build()

    private fun showResult(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}