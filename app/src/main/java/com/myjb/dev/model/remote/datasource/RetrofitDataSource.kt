package com.myjb.dev.model.remote.datasource

import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.model.remote.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import kotlin.system.measureTimeMillis

class RetrofitDataSource(private val apiService: ApiService) : DataSource {
    override suspend fun getImageUrls(): Flow<APIResponse> = flow {
        emit(APIResponse.Loading)

        try {
            val time = measureTimeMillis {
                val response = apiService.getPhotos()
                if (response.isSuccessful) {
                    val body = response.body()
                    Timber.e("[getImageUrls] list size : " + body?.size)
                    emit(APIResponse.Success(body!!))
                } else {
                    emit(
                        APIResponse.Error(
                            errorCode = response.code(),
                            message = response.message()
                        )
                    )
                }
            }
            Timber.e("[getImageUrls] total time : ${time}ms")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)
}