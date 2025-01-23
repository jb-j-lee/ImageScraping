package com.myjb.dev.model.remote.datasource

import android.util.Log
import com.myjb.dev.model.remote.APIResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.system.measureTimeMillis

private const val TAG = "RetrofitDataSource"

class RetrofitDataSource(private val apiService: ApiService) : DataSource {
    override suspend fun getImageUrls(url: String): Flow<APIResponse> = flow {
        emit(APIResponse.Loading)

        try {
            val time = measureTimeMillis {
                val result =
                    apiService.get(url.toHttpUrlOrNull()?.newBuilder("\\")?.build()!!).execute()
                        .body()
                Log.e(TAG, "[getImageUrls] list.size : " + result?.size)
                emit(APIResponse.Success(result!!))
            }
            Log.e(TAG, "[getImageUrls] time : $time")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)
}