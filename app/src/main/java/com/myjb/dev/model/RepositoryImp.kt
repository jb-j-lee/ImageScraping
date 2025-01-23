package com.myjb.dev.model

import com.myjb.dev.model.data.METHOD
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.model.remote.datasource.DataSource
import kotlinx.coroutines.flow.Flow

class RepositoryImp(
    private val jsoupDataSource: DataSource,
    private val retrofitDataSource: DataSource,
) : Repository {
    override suspend fun getImageUrls(method: METHOD, text: String): Flow<APIResponse> {
        return when (method) {
            METHOD.JSOUP -> {
                jsoupDataSource.getImageUrls(url = text)
            }

            METHOD.RETROFIT -> {
                retrofitDataSource.getImageUrls(url = text)
            }
        }
    }
}