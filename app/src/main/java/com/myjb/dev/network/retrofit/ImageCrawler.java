package com.myjb.dev.network.retrofit;

import android.util.Log;

import com.myjb.dev.myapplication.BuildConfig;
import com.myjb.dev.network.OnImageLinkListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by jblee on 2018-04-17.
 */

public class ImageCrawler {

    interface PageService {
        @GET
        Call<List<String>> get(@Url HttpUrl url);
    }

    private final static String TAG = "ImageCrawler";
    private final static int MAXIMUM_THREAD_COUNT = 20;

    private final PageService pageService;

    public ImageCrawler(HttpUrl baseUrl, final boolean isOnlySection) {
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(MAXIMUM_THREAD_COUNT));
        dispatcher.setMaxRequests(MAXIMUM_THREAD_COUNT);
        dispatcher.setMaxRequestsPerHost(1);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(interceptor);
        }

        OkHttpClient okHttpClient = okHttpBuilder
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(100, 3, TimeUnit.SECONDS))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new Converter.Factory() {
                    @Nullable
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        return new ImageAdapter(retrofit.baseUrl(), isOnlySection);
                    }
                })
                .client(okHttpClient)
                .build();

        this.pageService = retrofit.create(PageService.class);
    }

    public void crawlPage(HttpUrl url, final OnImageLinkListener callBack) {
        pageService.get(url).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, call.request().url() + " crawling is failed : " + response.code());
                    return;
                }

                if (BuildConfig.DEBUG)
                    Log.e(TAG, "[crawlPage] list.size : " + response.body().size());

                if (callBack != null)
                    callBack.onImageLinkResult(response.body());
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, call.request().url() + ": failed: " + t);
            }
        });
    }

    private class ImageAdapter implements Converter<ResponseBody, List<String>> {
        HttpUrl baseUrl;
        boolean isOnlySection;

        ImageAdapter(HttpUrl baseUrl, boolean isOnlySection) {
            this.baseUrl = baseUrl;
            this.isOnlySection = isOnlySection;
        }

        @Override
        public List<String> convert(ResponseBody responseBody) throws IOException {
            long init = System.currentTimeMillis();

            Document document = Jsoup.parse(responseBody.string());
            document.setBaseUri(baseUrl.toString());

            long parse = System.currentTimeMillis();
            checkTime("convert", "parse", init);

            String filter = null;
            if (isOnlySection)
                filter = "img[class=picture]";
            else
                filter = "img[src~=(?i)\\.(png|jpe?g|gif)]";
            Elements elements = document.select(filter);

            long select = System.currentTimeMillis();
            checkTime("convert", "select", parse);

            List<String> imageUrls = new ArrayList<>();
            for (Element element : elements) {
                imageUrls.add(element.absUrl("src"));
            }
            checkTime("convert", "add", select);

            return new ArrayList<>(imageUrls);
        }
    }

    private void checkTime(String method, String name, long previousTime) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, "[" + method + "] " + name + " : " + (System.currentTimeMillis() - previousTime));
    }
}