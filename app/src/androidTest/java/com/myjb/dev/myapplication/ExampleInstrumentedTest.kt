package com.myjb.dev.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.myjb.dev.model.Repository
import com.myjb.dev.model.data.METHOD
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest @Inject constructor(
    private val repository: Repository,
) {
    @Test
    @Throws(Exception::class)
    fun imageCrawlerTest() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        runBlocking {
            repository.getImageUrls(METHOD.RETROFIT, appContext.getString(R.string.target_url))
        }
    }

    @Test
    @Throws(Exception::class)
    fun imageScrapingTest() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        runBlocking {
            repository.getImageUrls(METHOD.JSOUP, appContext.getString(R.string.target_url))
        }
    }
}