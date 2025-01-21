package com.myjb.dev.network.jsoup

import android.util.Log
import com.myjb.dev.myapplication.BuildConfig
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException
import java.net.UnknownHostException

private const val TAG = "ImageScraping"

@Deprecated("TODO mvm")
class ImageScraping(private val url: String, private val isOnlySection: Boolean) {
    fun basicVersion(): List<String>? {
        try {
            val init = System.currentTimeMillis()

            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .header("scheme", "https")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("accept-encoding", "gzip, deflate, br")
                .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                .header("cache-control", "no-cache")
                .header("pragma", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .get()

//            val doc = con
//                .header(":authority", "pixabay.com")
//                .header(":method", "GET")
//                .header(":path", "/ko/photos/?q=test")
//                .header(":scheme", "https")
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
//                .header("Accept-encoding", "gzip, deflate, br, zstd")
//                .header("Accept-language", "ko-KR,ko;q=0.9")
//
//                .header("Dnt", "1")
//                .header("Priority", "u=0, i")
//                .header("Sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
//                .header("sec-ch-ua-mobile", "?0")
//
//                .header("sec-ch-ua-mobile", "?0")
//                .header("sec-ch-ua-platform", "\"Windows\"")
//                .header("sec-fetch-dest", "document")
//                .header("sec-fetch-mode", "navigate")
//                .header("sec-fetch-site", "none")
//                .header("sec-fetch-user", "?1")
//                .header("upgrade-insecure-requests", "1")
//
//                .get()

            val connect = System.currentTimeMillis()
            checkTime("getImageLinkUrl", "connect", init)

            val filter: String = if (isOnlySection) {
                "img[class=picture]"
            } else {
                "img[src~=(?i)\\.(png|jpe?g|gif)]"
            }
            val elements = doc.select(filter)

            val select = System.currentTimeMillis()
            checkTime("getImageLinkUrl", "select", connect)

            val imageUrls: MutableList<String> = ArrayList()
            for (element in elements) {
                imageUrls.add(element.absUrl("src"))
            }

            checkTime("getImageLinkUrl", "add", select)

            Log.e(TAG, "[getImageLinkUrl] list.size : " + imageUrls.size)
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

    private fun upgradeVersion(): List<String>? {
        try {
            val init = System.currentTimeMillis()

            val response = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute()

            val connect = System.currentTimeMillis()
            checkTime("getImageLinkUrl", "connect", init)

            val replaceBody = response.body()
                .replace("<(/?)(head|script|meta|ul|link|p|li)([^<>]*)>".toRegex(), "")
                .replace("<!--.*-->".toRegex(), "")
                .trim { it <= ' ' }

            val doc = Jsoup.parse(replaceBody)

            val filter = if (isOnlySection) {
                "img[class=picture]"
            } else {
                "img[src~=(?i)\\.(png|jpe?g|gif)]"
            }
            val elements = doc.select(filter)

            val select = System.currentTimeMillis()
            checkTime("getImageLinkUrl", "select", connect)

            val imageUrls: MutableList<String> = ArrayList()
            for (element in elements) {
                imageUrls.add(element.absUrl("src"))
            }

            checkTime("getImageLinkUrl", "add", select)

            Log.e(TAG, "[getImageLinkUrl] list.size : " + imageUrls.size)
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

    private fun checkTime(method: String, name: String, previousTime: Long) {
        if (BuildConfig.DEBUG) Log.e(
            TAG,
            "[" + method + "] " + name + " : " + (System.currentTimeMillis() - previousTime)
        )
    }
}