package com.studbudd.application_tracker.common.domain

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    var refreshToken
        get() = sharedPreferences.getString("refreshToken", null)
        set(value) {
            value?.let { sharedPreferences.edit().putString("refreshToken", it).apply() }
        }

    var accessToken
        get() = sharedPreferences.getString("accessToken", null)
        set(value) {
            value?.let { sharedPreferences.edit().putString("accessToken", it).apply() }
        }

    fun clearAllData() =
        sharedPreferences.edit().clear().commit()

}