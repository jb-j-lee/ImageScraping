package com.myjb.dev.myapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myjb.dev.model.Repository
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
        runBlocking {
            repository.getImageUrls()
        }
    }
}