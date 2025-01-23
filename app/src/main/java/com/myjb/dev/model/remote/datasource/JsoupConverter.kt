package com.myjb.dev.model.remote.datasource

import android.util.Log
import com.myjb.dev.myapplication.BuildConfig
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import java.io.IOException

private const val TAG = "JsoupConverter"

class JsoupConverter(private val url: String, private val isOnlySection: Boolean) :
    Converter<ResponseBody, List<String>> {

    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): List<String> {
        return parse(text = responseBody.string())
    }

    private fun parse(text: String): List<String> {
        val init = System.currentTimeMillis()

        val document = Jsoup.parse(text)
        //FIXME
//        document.setBaseUri(url)

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
            imageUrls.add(element.absUrl("src"))
        }
        checkTime(name = "add", time = select)

        return imageUrls
    }

    private fun checkTime(name: String, time: Long) {
        if (!BuildConfig.DEBUG) {
            return
        }

        Log.e(TAG, "[convert] $name : ${(System.currentTimeMillis() - time)}")
    }
}