package com.myjb.dev.model.remote.datasource

import android.util.Log
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.network.jsoup.ImageScraping
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.system.measureTimeMillis

private const val TAG = "JsoupRemoteDataSource"

object JsoupDataSource : DataSource {
    override suspend fun getImageUrls(text: String): Flow<APIResponse> = flow {
        emit(APIResponse.Loading)

        val time = measureTimeMillis {
            val result = ImageScraping(text, false).basicVersion()
            if (result == null) {
                emit(APIResponse.Error(0, ""))
                return@flow
            }

            emit(APIResponse.Success(result))
        }
        Log.e(TAG, "[getImageUrls] time : $time")
    }.flowOn(Dispatchers.IO)
}