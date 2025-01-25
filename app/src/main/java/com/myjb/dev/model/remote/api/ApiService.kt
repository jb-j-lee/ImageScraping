package com.myjb.dev.model.remote.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("{locale}/photos/search/{query}/")
    suspend fun getPhotos(
        @Path(value = "locale") locale: String = "ko",
        @Path(value = "query") query: String = "magic",
    ): Response<List<String>>
}