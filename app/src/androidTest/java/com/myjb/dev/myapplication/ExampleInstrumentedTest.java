package com.myjb.dev.myapplication;

import android.content.Context;
import android.os.AsyncTask;

import com.myjb.dev.network.OnImageLinkListener;
import com.myjb.dev.network.jsoup.ImageScraping;
import com.myjb.dev.network.retrofit.ImageCrawler;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import okhttp3.HttpUrl;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void ImageCrawlerTest() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

        ImageCrawler crawler = new ImageCrawler(HttpUrl.parse(appContext.getString(R.string.target_url)).newBuilder("\\").build(), false);
        crawler.crawlPage(HttpUrl.parse(appContext.getString(R.string.target_url)), new OnImageLinkListener() {
            @Override
            public void onImageLinkResult(List<String> imageUrls) {
                assertEquals(488, imageUrls.size());
            }
        });
    }

    @Test
    public void ImageScrapingTest() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

        new ImageScraping(appContext.getString(R.string.target_url), false, new OnImageLinkListener() {
            @Override
            public void onImageLinkResult(List<String> imageUrls) {
                assertEquals(488, imageUrls.size());
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
}
