package com.myjb.dev.model.remote.datasource

import android.util.Log
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.myapplication.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException
import java.net.UnknownHostException
import kotlin.system.measureTimeMillis

private const val TAG = "JsoupDataSource"

object JsoupDataSource : DataSource {
    override suspend fun getImageUrls(url: String): Flow<APIResponse> = flow {
        emit(APIResponse.Loading)

        try {
            val time = measureTimeMillis {
                val result = apiService(url, false)
                if (result == null) {
                    emit(APIResponse.Error(0, ""))
                    return@flow
                }
                Log.e(TAG, "[getImageUrls] list.size : " + result.size)

                emit(APIResponse.Success(result))
            }
            Log.e(TAG, "[getImageUrls] time : $time")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)

    private fun apiService(url: String, isOnlySection: Boolean): List<String>? {
        try {
            val init = System.currentTimeMillis()

            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .header("scheme", "https")
                .header(
                    "accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
                )
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                .header("cache-control", "no-cache")
                .header("pragma", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .get()

            val connect = System.currentTimeMillis()
            checkTime(name = "connect", time = init)

            val filter: String = if (isOnlySection) {
                "img[class=picture]"
            } else {
                "img[src~=(?i)\\.(png|jpe?g|gif)]"
            }
            val elements = doc.select(filter)

            val select = System.currentTimeMillis()
            checkTime(name = "select", time = connect)

            val imageUrls: MutableList<String> = ArrayList()
            for (element in elements) {
                imageUrls.add(element.absUrl("src"))
            }

            checkTime(name = "add", time = select)

            return imageUrls
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun checkTime(name: String, time: Long) {
        if (!BuildConfig.DEBUG) {
            return
        }

        Log.e(TAG, "[getImageLinkUrl] $name : ${(System.currentTimeMillis() - time)}")
    }
}