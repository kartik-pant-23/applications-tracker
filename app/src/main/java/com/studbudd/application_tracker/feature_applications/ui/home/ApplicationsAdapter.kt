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
import com.studbudd.application_tracker.databinding.ItemApplicationListHeaderBinding
import com.studbudd.application_tracker.feature_applications.ui.home.models.ApplicationListItem
import com.studbudd.application_tracker.feature_applications.ui.home.models.HeaderListItem
import com.studbudd.application_tracker.feature_applications.ui.home.models.ListItem

class ApplicationsAdapter(private val OnItemClickListener: (View, Long) -> Unit) :
    ListAdapter<ListItem, ApplicationsAdapter.ApplicationsViewHolder>(
        object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        }
    ) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }

    inner class ApplicationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationsViewHolder {
        val layoutResource =
            if (viewType == ListItem.VIEW_TYPE_HEADER) R.layout.item_application_list_header
            else R.layout.item_application
        val view: View =
            LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        return ApplicationsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApplicationsViewHolder, position: Int) {
        if (getItemViewType(position) == ListItem.VIEW_TYPE_HEADER) {
            val data = (getItem(position) as HeaderListItem).getTag()
            ItemApplicationListHeaderBinding.bind(holder.itemView).apply {
                tag.text = data
            }
        } else {
            val item = (getItem(position) as ApplicationListItem)
            val data = item.getApplication()
            val dividerVisibility = if (item.showDivider) View.VISIBLE else View.GONE
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

                divider.visibility = dividerVisibility

                root.setOnClickListener {
                    OnItemClickListener(it, data.id)
                }
            }
        }
    }

}