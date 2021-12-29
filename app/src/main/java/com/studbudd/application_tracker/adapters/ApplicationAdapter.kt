package com.studbudd.application_tracker.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.databinding.ItemApplicationBinding
import com.studbudd.application_tracker.utilities.DATE_FORMAT
import java.text.SimpleDateFormat

class ApplicationAdapter(private val OnItemClickListener: (View, Int) -> Unit) :
    ListAdapter<Application, ApplicationAdapter.ApplicationsViewHolder>(
        object : DiffUtil.ItemCallback<Application>() {
            override fun areItemsTheSame(oldItem: Application, newItem: Application): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Application, newItem: Application): Boolean {
                return oldItem.toString() == newItem.toString()
            }

        }
    ) {

    inner class ApplicationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return ApplicationsViewHolder(view)
    }

    private fun getStatusColor(statusId: Int): Int {
        return when (statusId) {
            0 -> Color.parseColor("#BFFFBB33")
            1 -> Color.parseColor("#BF80FF80")
            2 -> Color.parseColor("#BF99CC00")
            3 -> Color.parseColor("#BFFF8080")
            else -> Color.parseColor("#BFFF1FBC")
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        ItemApplicationBinding.bind(holder.itemView).apply {
            val application = getItem(position)
            applicationTitle.text = application.title
            notes.text =
                if (application.notes.isNullOrBlank()) application.jobLink else application.notes

            // Setting date
            applicationCreatedAt.text =
                DATE_FORMAT.format(application.created_at.timeInMillis).toString()

            // Setting background depending on status
            status.text = holder.itemView.context.resources
                .getStringArray(R.array.job_status)[application.status]
            status.backgroundTintList = ColorStateList.valueOf(getStatusColor(application.status))

            // Adding on click listener
            itemApplicationLayout.setOnClickListener{
                OnItemClickListener(it, application.application_id)
            }
        }
    }

}