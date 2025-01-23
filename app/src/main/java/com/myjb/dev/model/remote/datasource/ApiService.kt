package com.myjb.dev.model.remote.datasource

import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    fun get(@Url url: HttpUrl?): Call<List<String>>
}