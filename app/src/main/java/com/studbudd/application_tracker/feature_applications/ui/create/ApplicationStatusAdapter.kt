package com.studbudd.application_tracker.feature_applications.ui.create

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.ItemApplicationStatusSpinnerBinding
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus

class ApplicationStatusAdapter(
    private val context: Context,
    private val applicationStatuses: List<ApplicationStatus>
) : BaseAdapter(), SpinnerAdapter {

    override fun getCount(): Int {
        return applicationStatuses.size
    }

    override fun getItem(p0: Int): ApplicationStatus {
        return applicationStatuses[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return super.getDropDownView(position, convertView, parent)
    }

    override fun getView(position: Int, view: View?, container: ViewGroup?): View {
        val binding = ItemApplicationStatusSpinnerBinding.bind(
            view ?: LayoutInflater.from(context)
                .inflate(R.layout.item_application_status_spinner, container, false)
        )
        val item = getItem(position)
        val color: Int =
            if (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES) {
                item.colorNight
            } else {
                item.color
            }
        binding.jobStatusTag.text = item.tag
        binding.jobStatusBullet.backgroundTintList = ColorStateList.valueOf(color)
        binding.jobStatusTag.setTextColor(color)

        return binding.root
    }

}