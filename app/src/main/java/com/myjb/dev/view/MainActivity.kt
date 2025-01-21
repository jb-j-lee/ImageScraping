package com.myjb.dev.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.myjb.dev.model.data.METHOD
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.myapplication.R
import com.myjb.dev.myapplication.databinding.ActivityMainBinding
import com.myjb.dev.viewmodel.UrlViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: UrlViewModel by viewModels()

    private val adapter: UrlAdapter by lazy {
        UrlAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.recyclerView.setAdapter(adapter)
        binding.recyclerView.setItemAnimator(SlideInUpAnimator())

        val itemDecoration: ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)

        val targetUrl = getString(R.string.target_url)

        binding.swipeRefreshLayout.setOnRefreshListener {
            onRetrofitClicked(targetUrl)
        }

        binding.retrofit.setOnClickListener { onRetrofitClicked(targetUrl) }
        binding.jsoup.setOnClickListener { onJsoupClicked(targetUrl) }

        viewModel.result.observe(this) {
            when (it) {
                is APIResponse.Success -> {
                    binding.progressBar.hide()
                    binding.swipeRefreshLayout.isRefreshing = false

                    adapter.submitList(it.data)
                }

                APIResponse.Loading -> {
                    binding.progressBar.show()
                }

                is APIResponse.Error -> {
                    binding.progressBar.hide()
                    binding.swipeRefreshLayout.isRefreshing = false

                    adapter.submitList(emptyList())
                }
            }
        }
    }

    private fun onRetrofitClicked(targetUrl: String) {
        viewModel.getImageUrls(METHOD.RETROFIT, targetUrl)
    }

    private fun onJsoupClicked(targetUrl: String) {
        viewModel.getImageUrls(METHOD.JSOUP, targetUrl)
    }
}