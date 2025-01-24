package com.myjb.dev.model.remote.datasource

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("ko/photos/?q=test")
    fun getPhotos(): Call<List<String>>
}