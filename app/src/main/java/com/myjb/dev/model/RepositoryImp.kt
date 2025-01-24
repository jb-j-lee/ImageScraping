package com.myjb.dev.model

import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.model.remote.datasource.DataSource
import kotlinx.coroutines.flow.Flow

class RepositoryImp(
    private val retrofitDataSource: DataSource,
) : Repository {
    override suspend fun getImageUrls(): Flow<APIResponse> {
        return retrofitDataSource.getImageUrls()
    }
}