package com.example.android.kotlincoroutines

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.android.kotlincoroutines.main.RefreshMainDataWork
import java.util.concurrent.TimeUnit

class KotlinCoroutinesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupWorkManagerJob()
    }

    private fun setupWorkManagerJob() {
        val workManagerConfiguration = Configuration.Builder()
                .setWorkerFactory(RefreshMainDataWork.Factory())
                .build()
        WorkManager.initialize(this, workManagerConfiguration)

        val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiredNetworkType(UNMETERED)
                .build()
        val work = PeriodicWorkRequestBuilder<RefreshMainDataWork>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(RefreshMainDataWork::class.java.name, KEEP, work)
    }
}
