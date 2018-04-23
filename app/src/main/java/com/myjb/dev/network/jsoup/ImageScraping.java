package com.myjb.dev.network.jsoup;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.myjb.dev.myapplication.BuildConfig;
import com.myjb.dev.network.OnImageLinkListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ImageScraping extends AsyncTask<Void, Void, List<String>> {

    private final static String TAG = "ImageScraping";

    private String url;
    private boolean isOnlySection;
    private OnImageLinkListener listener;

    public ImageScraping(@NonNull String url, boolean isOnlySection, @NonNull OnImageLinkListener listener) {
        this.url = url;
        this.isOnlySection = isOnlySection;
        this.listener = listener;
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        return basicVersion();
    }

    @Override
    protected void onPostExecute(List<String> imageUrls) {
        if (listener != null)
            listener.onImageLinkResult(imageUrls);
    }

    private List<String> basicVersion() {
        try {
            long init = System.currentTimeMillis();

            Document doc = Jsoup.connect(url).get();

            long connect = System.currentTimeMillis();
            checkTime("getImageLinkUrl", "connect", init);

            String filter = null;
            if (isOnlySection)
                filter = "img[class=picture]";
            else
                filter = "img[src~=(?i)\\.(png|jpe?g|gif)]";
            Elements elements = doc.select(filter);

            long select = System.currentTimeMillis();
            checkTime("getImageLinkUrl", "select", connect);

            List<String> imageUrls = new ArrayList<String>();
            for (Element element : elements) {
                imageUrls.add(element.absUrl("src"));
            }

            checkTime("getImageLinkUrl", "add", select);

            Log.e(TAG, "[getImageLinkUrl] list.size : " + imageUrls.size());
            return imageUrls;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> upgradeVersion() {
        try {
            long init = System.currentTimeMillis();

            Connection.Response response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute();

            long connect = System.currentTimeMillis();
            checkTime("getImageLinkUrl", "connect", init);

            String replaceBody = response.body()
                    .replaceAll("<(/?)(head|script|meta|ul|link|p|li)([^<>]*)>", "")
                    .replaceAll("<!--.*-->", "")
                    .trim();

            Document doc = Jsoup.parse(replaceBody);

            String filter = null;
            if (isOnlySection)
                filter = "img[class=picture]";
            else
                filter = "img[src~=(?i)\\.(png|jpe?g|gif)]";
            Elements elements = doc.select(filter);

            long select = System.currentTimeMillis();
            checkTime("getImageLinkUrl", "select", connect);

            List<String> imageUrls = new ArrayList<String>();
            for (Element element : elements) {
                imageUrls.add(element.absUrl("src"));
            }

            checkTime("getImageLinkUrl", "add", select);

            Log.e(TAG, "[getImageLinkUrl] list.size : " + imageUrls.size());
            return imageUrls;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void checkTime(String method, String name, long previousTime) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, "[" + method + "] " + name + " : " + (System.currentTimeMillis() - previousTime));
    }
}
