package com.myjb.dev.myapplication

import com.myjb.dev.network.jsoup.ImageScraping
import com.myjb.dev.network.retrofit.ImageCrawler
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun imageCrawlerTest() {
        val targetUrl = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx"

        val crawler = ImageCrawler(targetUrl.toHttpUrlOrNull()?.newBuilder("\\")?.build()!!, false)
        crawler.crawlPage(targetUrl.toHttpUrlOrNull())
    }

    @Test
    @Throws(Exception::class)
    fun imageScrapingTest() {
        val targetUrl = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx"

        val imageUrls = ImageScraping(targetUrl, false).basicVersion()
        Assert.assertEquals(488, imageUrls?.size?.toLong())
    }
}