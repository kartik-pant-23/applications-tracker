package com.studbudd.application_tracker.feature_applications.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.loadImageFromUrl
import com.studbudd.application_tracker.databinding.ItemApplicationBinding
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

class ApplicationsAdapter(private val OnItemClickListener: (View, Long) -> Unit) :
    ListAdapter<JobApplication, ApplicationsAdapter.ApplicationsViewHolder>(
        object : DiffUtil.ItemCallback<JobApplication>() {
            override fun areContentsTheSame(
                oldItem: JobApplication,
                newItem: JobApplication
            ): Boolean {
                return oldItem.toString() == newItem.toString()
            }

            override fun areItemsTheSame(
                oldItem: JobApplication,
                newItem: JobApplication
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    inner class ApplicationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return ApplicationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        val data = getItem(position)
        ItemApplicationBinding.bind(holder.itemView).apply {
            companyLogoText.text = data.job.company.first().toString()
            if (data.job.companyLogo != null) {
                companyLogoImg.loadImageFromUrl(data.job.companyLogo, R.drawable.ic_company)
                companyLogoImg.visibility = View.VISIBLE
                companyLogoText.visibility = View.GONE
            } else {
                companyLogoImg.visibility = View.GONE
                companyLogoText.visibility = View.VISIBLE
            }

            companyName.text = data.job.company
            jobRole.text = data.job.role

            applicationStatus.dividerColor = data.status.getColor(holder.itemView.context)

            createAtDay.text = data.createdAtDay
            createAtMonthYear.text = data.createdAtMonthYear

            root.setOnClickListener {
                OnItemClickListener(it, data.id)
            }
        }
    }

}