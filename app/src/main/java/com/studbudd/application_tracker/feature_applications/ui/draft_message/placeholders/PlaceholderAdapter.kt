package com.studbudd.application_tracker.feature_applications.ui.draft_message.placeholders

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.showError
import com.studbudd.application_tracker.databinding.ItemPlaceholderBinding

class PlaceholderAdapter(private val onRemoveItem: (position: Int) -> Unit) :
    ListAdapter<ItemPlaceholder, PlaceholderAdapter.PlaceholderViewHolder>(
        object : DiffUtil.ItemCallback<ItemPlaceholder>() {
            override fun areContentsTheSame(
                oldItem: ItemPlaceholder,
                newItem: ItemPlaceholder
            ): Boolean {
                return oldItem.toString() == newItem.toString()
            }

            override fun areItemsTheSame(
                oldItem: ItemPlaceholder,
                newItem: ItemPlaceholder
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class PlaceholderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemPlaceholderBinding.bind(itemView)

        init {
            with(binding) {
                placeholderKey.addTextChangedListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        getItem(adapterPosition).key = it.toString()
                }
                placeholderValue.addTextChangedListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        getItem(adapterPosition).value = it.toString()
                }
            }
        }
    }

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

    private fun getIconColor(context: Context, isEditable: Boolean): Int {
        return if (isEditable) ContextCompat.getColor(context, R.color.dark_text)
        else ContextCompat.getColor(context, R.color.light_text)
    }

    override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
        val itemView = holder.itemView
        val item = getItem(position)
        with(holder.binding) {
            placeholderKey.apply {
                setText(item.key)
                isEnabled = item.isEditable
                item.keyErrorMessage?.let { showError(it) }
            }

            placeholderValue.apply {
                setText(item.value)
                isEnabled = item.isEditable
            }

            removePlaceholder.visibility = if (item.isEditable) View.VISIBLE else View.GONE

            iconMapped.apply {
                setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "<${item.key}> gets replaced by '${item.value}'.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ImageViewCompat.setImageTintList(
                    this,
                    ColorStateList.valueOf(
                        getIconColor(
                            context = itemView.context,
                            isEditable = item.isEditable
                        )
                    )
                )
            }

            removePlaceholder.setOnClickListener {
                onRemoveItem(position)
                this@PlaceholderAdapter.notifyItemRemoved(position)
            }
        }
    }

}