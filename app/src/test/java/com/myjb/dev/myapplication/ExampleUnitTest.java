package com.myjb.dev.myapplication;

import android.content.Context;
import android.os.AsyncTask;

import com.myjb.dev.network.OnImageLinkListener;
import com.myjb.dev.network.jsoup.ImageScraping;
import com.myjb.dev.network.retrofit.ImageCrawler;

import org.junit.Test;

import java.util.List;

import okhttp3.HttpUrl;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void ImageCrawlerTest() throws Exception {
        String targetUrl = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";

        ImageCrawler crawler = new ImageCrawler(HttpUrl.parse(targetUrl).newBuilder("\\").build(), false);
        crawler.crawlPage(HttpUrl.parse(targetUrl), new OnImageLinkListener() {
            @Override
            public void onImageLinkResult(List<String> imageUrls) {
                assertEquals(488, imageUrls.size());
            }
        });
    }

    @Test
    public void ImageScrapingTest() throws Exception {
        String targetUrl = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";

        new ImageScraping(targetUrl, false, new OnImageLinkListener() {
            @Override
            public void onImageLinkResult(List<String> imageUrls) {
                assertEquals(488, imageUrls.size());
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
}