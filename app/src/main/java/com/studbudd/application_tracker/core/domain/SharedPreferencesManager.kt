package com.studbudd.application_tracker.core.domain

import android.content.SharedPreferences

class SharedPreferencesManager(
    private val sharedPreferences: SharedPreferences
) {

    var refreshToken
        get() = sharedPreferences.getString(REFRESH_TOKEN, null)
        set(value) {
            value?.let { sharedPreferences.edit().putString(REFRESH_TOKEN, it).apply() }
        }

    var accessToken
        get() = sharedPreferences.getString(ACCESS_TOKEN, null)
        set(value) {
            value?.let { sharedPreferences.edit().putString(ACCESS_TOKEN, it).apply() }
        }

    var draftMessage
        get() = sharedPreferences.getString(DRAFT_MESSAGE, null)
        set(value) {
            value?.let { sharedPreferences.edit().putString(DRAFT_MESSAGE, it).apply() }
        }

    fun clearAllData() =
        sharedPreferences.edit().clear().commit()

    companion object {
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val DRAFT_MESSAGE = "defaultMessage"
    }

}