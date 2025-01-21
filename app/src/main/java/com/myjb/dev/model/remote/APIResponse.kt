package com.myjb.dev.model.remote

sealed class APIResponse {
    data class Success(val data: List<String>) : APIResponse()
    data class Error(val errorCode: Int, val message: String) : APIResponse()
    data object Loading : APIResponse()
}