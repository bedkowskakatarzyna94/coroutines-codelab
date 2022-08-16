package com.example.android.kotlincoroutines.main

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class RefreshMainDataWork(
    context: Context,
    params: WorkerParameters,
    private val network: MainNetwork
) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = TitleRepository(network, database.titleDao)

        return try {
            repository.refreshTitle()
            Result.success()
        } catch (error: TitleRepository.TitleRefreshError) {
            Result.failure()
        }
    }

    class Factory(val network: MainNetwork = getNetworkService()) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return RefreshMainDataWork(appContext, workerParameters, network)
        }

    }
}