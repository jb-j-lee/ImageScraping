package com.myjb.dev.model

import com.myjb.dev.model.data.METHOD
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.model.remote.datasource.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImp(
    private val jsoupDataSource: DataSource,
    private val retrofitDataSource: DataSource,
) : Repository {
    override suspend fun getImageUrls(method: METHOD, text: String): Flow<APIResponse> {
        return when (method) {
            METHOD.JSOUP -> {
                jsoupDataSource.getImageUrls(text = text)
            }

            METHOD.RETROFIT -> {
                retrofitDataSource.getImageUrls(text = text)
            }

            else -> {
                flowOf(APIResponse.Error(errorCode = 0, message = ""))
            }
        }
    }
}