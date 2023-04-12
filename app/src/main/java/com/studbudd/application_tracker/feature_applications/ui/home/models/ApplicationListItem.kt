package com.studbudd.application_tracker.feature_applications.ui.home.models

import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication

class ApplicationListItem(
    private val application: JobApplication,
) : ListItem() {

    var showDivider = true

    fun getApplication(): JobApplication {
        return application
    }

    override fun getViewType(): Int {
        return VIEW_TYPE_APPLICATION_ITEM
    }

}