package com.studbudd.application_tracker.feature_applications.ui.draft_message.placeholders

data class ItemPlaceholder(
    var key: String,
    var value: String,
    val type: Int,
    val keyErrorMessage: String? = null,
) {

    val isEditable = type == PLACEHOLDER_DATA

    companion object {
        const val APPLICATION_DATA = 0
        const val PLACEHOLDER_DATA = 1
    }

}
