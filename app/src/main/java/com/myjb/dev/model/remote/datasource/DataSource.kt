package com.myjb.dev.model.remote.datasource

import com.myjb.dev.model.remote.APIResponse
import kotlinx.coroutines.flow.Flow

interface DataSource {
    suspend fun getImageUrls(text: String): Flow<APIResponse>
}