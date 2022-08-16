package com.example.android.kotlincoroutines.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

class TitleRepository(val network: MainNetwork, val titleDao: TitleDao) {
    val title: LiveData<String?> = titleDao.titleLiveData.map { it?.title }

    suspend fun refreshTitle() {
        try {
            val result = network.fetchNextTitle()
            titleDao.insertTitle(Title(result))
        } catch (cause: Throwable) {
            throw TitleRefreshError("Unable to refresh title", cause)
        }
    }

    class TitleRefreshError(message: String, cause: Throwable?) : Throwable(message, cause)

}
