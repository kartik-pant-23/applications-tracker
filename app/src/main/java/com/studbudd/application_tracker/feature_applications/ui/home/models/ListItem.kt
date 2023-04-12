package com.studbudd.application_tracker.feature_applications.ui.home.models

abstract class ListItem {

    abstract fun getViewType(): Int

    companion object {
        const val VIEW_TYPE_APPLICATION_ITEM = 0
        const val VIEW_TYPE_HEADER = 1
    }
}