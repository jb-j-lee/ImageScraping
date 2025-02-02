package com.myjb.dev.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.myjb.dev.model.remote.APIResponse
import com.myjb.dev.myapplication.BuildConfig
import com.myjb.dev.myapplication.databinding.ActivityMainBinding
import com.myjb.dev.viewmodel.ScrapingViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.palaima.debugdrawer.DebugDrawer
import io.palaima.debugdrawer.commons.DeviceModule
import io.palaima.debugdrawer.logs.LogsModule
import io.palaima.debugdrawer.timber.TimberModule
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: ScrapingViewModel by viewModels()

    private val adapter: ScrapingAdapter by lazy {
        ScrapingAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (BuildConfig.DEBUG) {
            enableDebugDrawable()
        }

        with(binding) {
            lifecycleOwner = this@MainActivity
            model = viewModel

            recyclerView.setAdapter(adapter)
            recyclerView.setItemAnimator(SlideInUpAnimator())

            val itemDecoration: ItemDecoration =
                DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
            recyclerView.addItemDecoration(itemDecoration)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            onRetrofitClicked()
        }

        binding.retrofit.setOnClickListener { onRetrofitClicked() }

        viewModel.result.observe(this) {
            when (it) {
                is APIResponse.Success -> {
                    hideProgress()

                    adapter.submitList(it.data)
                }

                APIResponse.Loading -> {
                    showProgress()
                }

                is APIResponse.Error -> {
                    hideProgress()

                    adapter.submitList(emptyList())
                }
            }
        }
    }

    private fun onRetrofitClicked() {
        viewModel.getImageUrls()
    }

    private fun showProgress() {
        binding.progressBar.show()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun hideProgress() {
        binding.progressBar.hide()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun enableDebugDrawable() {
        DebugDrawer.Builder(this).modules(
            DeviceModule(),

            LogsModule(),
            TimberModule(application.packageName + ".provider"),
        ).build()
    }
}