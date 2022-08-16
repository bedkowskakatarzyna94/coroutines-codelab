package com.example.android.kotlincoroutines.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.kotlincoroutines.main.MainNetwork
import com.example.android.kotlincoroutines.main.Title
import com.example.android.kotlincoroutines.main.TitleDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class TitleDaoFake(initialTitle: String) : TitleDao {

    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    override suspend fun insertTitle(title: Title) {
        insertedForNext.send(title)
        _titleLiveData.value = title
    }

    private val _titleLiveData = MutableLiveData<Title?>(Title(initialTitle))

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    fun nextInsertedOrNull(timeout: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            try {
                withTimeout(timeout) {
                    result = insertedForNext.receive().title
                }
            } catch (ex: TimeoutCancellationException) {
            }
        }
        return result
    }
}

class MainNetworkFake(var result: String) : MainNetwork {
    override suspend fun fetchNextTitle() = result
}

class MainNetworkCompletableFake() : MainNetwork {
    private var completable = CompletableDeferred<String>()

    override suspend fun fetchNextTitle(): String = completable.await()

    fun sendCompletionToAllCurrentRequests(result: String) {
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequests(throwable: Throwable) {
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }

}