package com.myjb.dev.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myjb.dev.myapplication.databinding.ItemTextviewBinding

class ScrapingAdapter(private val context: Context) :
    ListAdapter<String, UrlViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        return UrlViewHolder(
            ItemTextviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: UrlViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}

class UrlViewHolder(private val binding: ItemTextviewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String) {
        binding.model = item

        binding.executePendingBindings()
    }
}

object DiffUtilCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}