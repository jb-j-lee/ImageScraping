package com.myjb.dev.extension

import android.view.View
import android.widget.ImageView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.myjb.dev.model.remote.APIResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@BindingAdapter("progressShow")
fun ContentLoadingProgressBar.bindShow(response: APIResponse?) {
    visibility = if (response is APIResponse.Loading) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("imageShow")
fun ImageView.bindShow(response: APIResponse?) {
    visibility =
        if (response is APIResponse.Success && response.data.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String) {
    Timber.e("url : $url")

    if (url.isNotBlank()) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(this@setImageUrl)
                    .applyDefaultRequestOptions(
                        RequestOptions()
                            .format(DecodeFormat.PREFER_RGB_565)
                            .disallowHardwareConfig()
                    )
                    .asBitmap()
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .submit().get()
            }

            this@setImageUrl.setImageBitmap(bitmap)
        }
    } else {
        setImageResource(android.R.drawable.ic_dialog_info)
    }
}