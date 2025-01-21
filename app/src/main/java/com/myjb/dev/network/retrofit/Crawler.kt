package com.myjb.dev.network.retrofit

import android.util.Log
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.IOException
import java.lang.reflect.Type
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class Crawler(url: String) {
    internal interface PageService {
        @GET
        fun get(@Url url: HttpUrl?): Call<Page?>
    }

    internal inner class Page(val title: String, val links: List<String>)

    private val fetchedUrls: MutableSet<HttpUrl> = Collections.synchronizedSet(LinkedHashSet())
    private val hostnames = ConcurrentHashMap<String, AtomicInteger>()
    private val pageService: PageService

    fun crawlPage(url: HttpUrl) {
        // Skip hosts that we've visited many times.
        var hostnameCount = AtomicInteger()
        val previous = hostnames.putIfAbsent(url.host, hostnameCount)

        if (previous != null) hostnameCount = previous

        if (hostnameCount.incrementAndGet() > MAXIMUM_URL_LINK_COUNT) {
            Log.w(TAG, url.host + " can not be added to the maximum number of links.")
            return
        }

        // Asynchronously visit URL.
        pageService.get(url).enqueue(object : Callback<Page?> {
            override fun onResponse(call: Call<Page?>, response: Response<Page?>) {
                if (!response.isSuccessful) {
                    Log.e(
                        TAG,
                        call.request().url.toString() + " crawling is failed : " + response.code()
                    )
                    return
                }

                // Print this page's URL and title.
                val page = response.body()
                val base = response.raw().request.url
                Log.d(TAG, base.toString() + ": " + page!!.title)

                // Enqueue its links for visiting.
                for (link in page.links) {
                    val linkUrl = base.resolve(link)
                    if (linkUrl != null && fetchedUrls.add(linkUrl)) {
                        crawlPage(linkUrl)
                    }
                }
            }

            override fun onFailure(call: Call<Page?>, t: Throwable) {
                Log.e(TAG, call.request().url.toString() + ": failed: " + t)
            }
        })
    }

    private var factory: Converter.Factory = object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit,
        ): Converter<ResponseBody, *>? {
            if (type === Page::class.java) return PageAdapter()
            return null
        }
    }

    init {
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(MAXIMUM_THREAD_COUNT))
        dispatcher.maxRequests = MAXIMUM_THREAD_COUNT
        dispatcher.maxRequestsPerHost = 1

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(100, 30, TimeUnit.SECONDS))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(factory)
            .client(okHttpClient)
            .build()

        this.pageService = retrofit.create(
            PageService::class.java
        )
    }

    internal inner class PageAdapter : Converter<ResponseBody, Page> {
        @Throws(IOException::class)
        override fun convert(responseBody: ResponseBody): Page {
            val document = Jsoup.parse(responseBody.string())

            val links: MutableList<String> = ArrayList()
            val elements = document.select("a[href]")
            for (element in elements) {
                links.add(element.attr("href"))
            }
            return Page(document.title(), Collections.unmodifiableList(links))
        }
    }

    companion object {
        private const val TAG = "Crawler"

        private const val MAXIMUM_URL_LINK_COUNT = 2
        private const val MAXIMUM_THREAD_COUNT = 20
    }
}