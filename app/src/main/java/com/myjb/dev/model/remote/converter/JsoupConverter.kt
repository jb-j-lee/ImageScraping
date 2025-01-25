package com.myjb.dev.model.remote.converter

import com.myjb.dev.myapplication.BuildConfig
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import timber.log.Timber
import java.io.IOException

class JsoupConverter(private val isOnlySection: Boolean) :
    Converter<ResponseBody, List<String>> {

    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): List<String> {
        return parse(text = responseBody.string())
    }

    private fun parse(text: String): List<String> {
        val init = System.currentTimeMillis()

        val document = Jsoup.parse(text)

        val parse = System.currentTimeMillis()
        checkTime(name = "parse", time = init)
        val filter = if (isOnlySection) {
            "img[class=picture]"
        } else {
            "img[src~=(?i)\\.(png|jpe?g|gif)]"
        }
        val elements = document.select(filter)

        val select = System.currentTimeMillis()
        checkTime(name = "select", time = parse)

        val imageUrls: MutableList<String> = ArrayList()
        for (element in elements) {
            val src = element.absUrl("src")
            val url = isImage(src)
            if (!url.isNullOrBlank()) {
                imageUrls.add(url)
            }
        }
        checkTime(name = "add", time = select)

        return imageUrls
    }

    private fun isImage(url: String): String? {
        if (url.isBlank()) {
            return null
        }

        if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            return url.replace("&amp;", "&")
        }

        return null
    }

    private fun checkTime(name: String, time: Long) {
        if (!BuildConfig.DEBUG) {
            return
        }

        Timber.e("[convert] $name : ${(System.currentTimeMillis() - time)}ms")
    }
}