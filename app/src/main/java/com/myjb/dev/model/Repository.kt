package com.myjb.dev.model

import com.myjb.dev.model.data.METHOD
import com.myjb.dev.model.remote.APIResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getImageUrls(method: METHOD, text: String): Flow<APIResponse>
}