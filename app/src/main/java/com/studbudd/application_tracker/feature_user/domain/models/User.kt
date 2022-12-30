package com.studbudd.application_tracker.feature_user.domain.models

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

data class User(
    private val remoteId: String? = null,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val placeholderKeys: List<String>,
    val placeholderValues: List<String>,
    private val createdAt: String? = null
) {

    val joinedOn: String
        get() {
            return createdAt?.let {
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(it)
                    if (date != null) SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH).format(date) else "-"
                } catch (e: Exception) {
                    Log.e("User", "joinedOn date cannot be parsed, createdAt=$createdAt")
                    "-"
                }
            } ?: "-"
        }

    val isAnonymousUser = remoteId == null

}
