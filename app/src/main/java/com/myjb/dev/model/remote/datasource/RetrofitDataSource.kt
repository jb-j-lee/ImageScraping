package com.myjb.dev.model.remote.datasource

import android.util.Log
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.network.retrofit.ImageCrawler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlin.system.measureTimeMillis

private const val TAG = "RetrofitRemoteDataSource"

object RetrofitDataSource : DataSource {
    override suspend fun getImageUrls(text: String): Flow<APIResponse> = flow {
        emit(APIResponse.Loading)

        val time = measureTimeMillis {
            val crawler = ImageCrawler(text.toHttpUrlOrNull()?.newBuilder("\\")?.build()!!, false)
            val result = crawler.crawlPage(text.toHttpUrlOrNull())
            emit(APIResponse.Success(result!!))
        }
        Log.e(TAG, "[getImageUrls] time : $time")
    }.flowOn(Dispatchers.IO)
}