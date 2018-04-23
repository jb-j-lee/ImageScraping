package com.myjb.dev.network.retrofit;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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

public class Crawler {

    interface PageService {
        @GET
        Call<Page> get(@Url HttpUrl url);
    }

    class Page {
        final String title;
        final List<String> links;

        Page(String title, List<String> links) {
            this.title = title;
            this.links = links;
        }
    }

    private final static String TAG = "Crawler";

    private final static int MAXIMUM_URL_LINK_COUNT = 2;
    private final static int MAXIMUM_THREAD_COUNT = 20;

    private final Set<HttpUrl> fetchedUrls = Collections.synchronizedSet(new LinkedHashSet<HttpUrl>());
    private final ConcurrentHashMap<String, AtomicInteger> hostnames = new ConcurrentHashMap<>();
    private final PageService pageService;

    public Crawler(String url) {
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(MAXIMUM_THREAD_COUNT));
        dispatcher.setMaxRequests(MAXIMUM_THREAD_COUNT);
        dispatcher.setMaxRequestsPerHost(1);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(FACTORY)
                .client(okHttpClient)
                .build();

        this.pageService = retrofit.create(PageService.class);
    }

    public void crawlPage(HttpUrl url) {
        // Skip hosts that we've visited many times.
        AtomicInteger hostnameCount = new AtomicInteger();
        AtomicInteger previous = hostnames.putIfAbsent(url.host(), hostnameCount);

        if (previous != null)
            hostnameCount = previous;

        if (hostnameCount.incrementAndGet() > MAXIMUM_URL_LINK_COUNT) {
            Log.w(TAG, url.host() + " can not be added to the maximum number of links.");
            return;
        }

        // Asynchronously visit URL.
        pageService.get(url).enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, call.request().url() + " crawling is failed : " + response.code());
                    return;
                }

                // Print this page's URL and title.
                Page page = response.body();
                HttpUrl base = response.raw().request().url();
                Log.d(TAG, base + ": " + page.title);

                // Enqueue its links for visiting.
                for (String link : page.links) {
                    HttpUrl linkUrl = base.resolve(link);
                    if (linkUrl != null && fetchedUrls.add(linkUrl)) {
                        crawlPage(linkUrl);
                    }
                }
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Log.e(TAG, call.request().url() + ": failed: " + t);
            }
        });
    }

    Converter.Factory FACTORY = new Converter.Factory() {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (type == Page.class)
                return new PageAdapter();
            return null;
        }
    };

    class PageAdapter implements Converter<ResponseBody, Page> {
        @Override
        public Page convert(ResponseBody responseBody) throws IOException {
            Document document = Jsoup.parse(responseBody.string());

            List<String> links = new ArrayList<>();
            Elements elements = document.select("a[href]");
            for (Element element : elements) {
                links.add(element.attr("href"));
            }
            return new Page(document.title(), Collections.unmodifiableList(links));
        }
    }
}