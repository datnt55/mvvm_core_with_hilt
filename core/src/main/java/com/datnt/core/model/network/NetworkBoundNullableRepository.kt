package com.datnt.core.model.network

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * A repository which provides resource from local database as well as remote end point.
 *
 * [T] type for network.
 */
@ExperimentalCoroutinesApi
abstract class NetworkBoundNullableRepository<T> {

    fun asFlow() = flow {

        // Emit Loading State
        emit(ResponseResult.Loading)

        try {

            // Fetch latest posts from remote
            val apiResponse = fetchFromRemote()

            // Parse body
            val remotePosts = apiResponse.body()

            // Check for response validation
            if (apiResponse.isSuccessful) {
                // Save posts into the persistence storage
                remotePosts?.let {
                    handleRemoteData(it)
                }

                emit(ResponseResult.Success(remotePosts))
            } else {
                emit(
                    ResponseResult.Error<T>(
                        apiResponse.code(),
                        apiResponse.errorBody()?.string().toString()
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ERROR",e.message.toString())
            emit(ResponseResult.Error<T>(exception = e.message.toString()))
        }
    }

    /**
     * Saves retrieved from remote into the persistence storage.
     */
    @WorkerThread
    open suspend fun handleRemoteData(response: T) {
    }

    /**
     * Fetches [Response] from the remote end point.
     */
    @MainThread
    protected abstract suspend fun fetchFromRemote(): Response<T>


}
