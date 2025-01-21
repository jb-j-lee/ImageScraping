package com.myjb.dev.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.myjb.dev.network.jsoup.ImageScraping
import com.myjb.dev.network.retrofit.ImageCrawler
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    @Throws(Exception::class)
    fun imageCrawlerTest() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val url = appContext.getString(R.string.target_url).toHttpUrlOrNull()

        val crawler = ImageCrawler(url?.newBuilder("\\")?.build()!!, false)
        crawler.crawlPage(url)
    }

    @Test
    @Throws(Exception::class)
    fun imageScrapingTest() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val imageUrls =
            ImageScraping(appContext.getString(R.string.target_url), false).basicVersion()
        Assert.assertEquals(488, imageUrls?.size?.toLong())
    }
}