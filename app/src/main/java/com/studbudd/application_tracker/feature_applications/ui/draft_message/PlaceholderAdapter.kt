package com.studbudd.application_tracker.feature_applications.ui.draft_message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.ItemPlaceholderBinding

class PlaceholderAdapter :
    ListAdapter<Pair<String, String>, PlaceholderAdapter.PlaceholderViewHolder>(
        object : DiffUtil.ItemCallback<Pair<String, String>>() {
            override fun areContentsTheSame(
                oldItem: Pair<String, String>,
                newItem: Pair<String, String>
            ): Boolean {
                return oldItem.toString() == newItem.toString()
            }

            override fun areItemsTheSame(
                oldItem: Pair<String, String>,
                newItem: Pair<String, String>
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class PlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
        return PlaceholderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_placeholder,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
        val itemView = holder.itemView
        with(ItemPlaceholderBinding.bind(itemView)) {
            placeholderKey.text = getItem(position).first
            placeholderValue.text = getItem(position).second
        }
    }

}