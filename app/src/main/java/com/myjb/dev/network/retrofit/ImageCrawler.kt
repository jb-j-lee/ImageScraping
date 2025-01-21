package com.myjb.dev.network.retrofit

import android.util.Log
import com.myjb.dev.myapplication.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val TAG = "ImageCrawler"
private const val MAXIMUM_THREAD_COUNT = 20

@Deprecated("TODO mvm")
class ImageCrawler(baseUrl: HttpUrl, isOnlySection: Boolean) {
    internal interface PageService {
        @GET
        fun get(@Url url: HttpUrl?): Call<List<String>>
    }

    private val pageService: PageService

    init {
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(MAXIMUM_THREAD_COUNT))
        dispatcher.maxRequests = MAXIMUM_THREAD_COUNT
        dispatcher.maxRequestsPerHost = 1

        val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpBuilder.addInterceptor(interceptor)
        }

        val okHttpClient: OkHttpClient = okHttpBuilder
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(100, 3, TimeUnit.SECONDS))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(object : Converter.Factory() {
                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<Annotation>,
                    retrofit: Retrofit,
                ): Converter<ResponseBody, *> {
                    return ImageAdapter(retrofit.baseUrl(), isOnlySection)
                }
            })
            .client(okHttpClient)
            .build()

        this.pageService = retrofit.create(
            PageService::class.java
        )
    }

    @Throws(IOException::class)
    fun crawlPage(url: HttpUrl?): List<String>? {
        val result = pageService.get(url).execute()
        return result.body()
    }

    private inner class ImageAdapter(var baseUrl: HttpUrl, var isOnlySection: Boolean) :
        Converter<ResponseBody, List<String>> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): List<String> {
            val init = System.currentTimeMillis()

            val document = Jsoup.parse(responseBody.string())
            document.setBaseUri(baseUrl.toString())

            val parse = System.currentTimeMillis()
            checkTime("convert", "parse", init)
            val filter = if (isOnlySection) "img[class=picture]"
            else "img[src~=(?i)\\.(png|jpe?g|gif)]"
            val elements = document.select(filter)

            val select = System.currentTimeMillis()
            checkTime("convert", "select", parse)

            val imageUrls: MutableList<String> = ArrayList()
            for (element in elements) {
                imageUrls.add(element.absUrl("src"))
            }
            checkTime("convert", "add", select)

            return ArrayList(imageUrls)
        }
    }

    private fun checkTime(method: String, name: String, previousTime: Long) {
        if (BuildConfig.DEBUG) Log.e(
            TAG,
            "[" + method + "] " + name + " : " + (System.currentTimeMillis() - previousTime)
        )
    }
}