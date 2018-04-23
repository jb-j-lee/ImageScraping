package com.myjb.dev.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myjb.dev.network.OnImageLinkListener;
import com.myjb.dev.network.jsoup.ImageScraping;
import com.myjb.dev.network.retrofit.ImageCrawler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.HttpUrl;

public class ScrollingActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ImageAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.retrofit)
    FloatingActionButton retrofit;

    @BindView(R.id.jsoup)
    FloatingActionButton jsoup;

    @BindString(R.string.target_url)
    String target_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        ButterKnife.setDebug(BuildConfig.DEBUG);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        adapter = new ImageAdapter(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this){
            //Issue : NestedScrollView with RecyclerView
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @OnClick(R.id.retrofit)
    public void onRetrofitClicked(View view) {
        Snackbar.make(view, R.string.action_retrofit, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_Run, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.clearList();

                        ImageCrawler crawler = new ImageCrawler(HttpUrl.parse(target_url).newBuilder("\\").build(), false);
                        crawler.crawlPage(HttpUrl.parse(target_url), new OnImageLinkListener() {
                            @Override
                            public void onImageLinkResult(List<String> imageUrls) {
                                adapter.updateList(imageUrls, true);
                            }
                        });
                    }
                })
                .show();
    }

    @OnClick(R.id.jsoup)
    public void onJsoupClicked(View view) {
        Snackbar.make(view, R.string.action_jsoup, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_Run, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.clearList();

                        new ImageScraping(target_url, false, new OnImageLinkListener() {
                            @Override
                            public void onImageLinkResult(List<String> imageUrls) {
                                adapter.updateList(imageUrls, false);
                            }
                        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                })
                .show();
    }

    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

        private Context context;
        private List<String> list = new ArrayList<>();
        private boolean isRetrofit = false;

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.text)
            public TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
//                textView = (TextView) itemView.findViewById(R.id.text);
            }
        }

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_textview, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
            TextView textView = holder.textView;
            String item = list.get(position);
            if (isRetrofit)
                textView.setTextColor(Color.GREEN);
            else
                textView.setTextColor(Color.BLUE);
            textView.setText("[" + position + "] " + item);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void updateList(List<String> list, boolean isRetrofit) {
            if(this.list.size() > 0)
                this.list.clear();

            this.list.addAll(list);
            this.isRetrofit = isRetrofit;
            notifyDataSetChanged();
        }

        public void clearList() {
            this.list.clear();
            notifyDataSetChanged();
        }
    }
}
