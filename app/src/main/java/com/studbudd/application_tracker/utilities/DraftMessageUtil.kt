package com.studbudd.application_tracker.utilities

import android.content.SharedPreferences
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class DraftMessageUtil @Inject constructor() {
    @Inject lateinit var sharedPref: SharedPreferences

    val draftMessage: String =
        sharedPref.getString(DRAFT_MSG_KEY, DEFAULT_DRAFT_MESSAGE) ?: DEFAULT_DRAFT_MESSAGE

    lateinit var name: String
        private set
    lateinit var degree: String
        private set
    lateinit var college: String
        private set
    lateinit var experience: String
        private set
    lateinit var resume: String
        private set

    init {
        getValues()
    }

    private fun getValues() {
        name = sharedPref.getString(PLACEHOLDER_NAME_KEY, "<name>") ?: "<name>"
        degree = sharedPref.getString(PLACEHOLDER_DEGREE_KEY, "<degree>") ?: "<degree>"
        college = sharedPref.getString(PLACEHOLDER_COLLEGE_KEY, "<college>") ?: "<college>"
        experience =
            sharedPref.getString(PLACEHOLDER_EXPERIENCE_KEY, "<experience>") ?: "<experience>"
        resume = sharedPref.getString(PLACEHOLDER_RESUME_KEY, "<resume>") ?: "<resume>"
    }

    fun getPreviewMessage(msg: String, jobLink: String): String {
        getValues()
        var message = msg.replace("<job-link>", jobLink)
        message = message.replace("<name>", name)
        message = message.replace("<degree>", degree)
        message = message.replace("<college>", college)
        message = message.replace("<experience>", experience)
        message = message.replace("<resume>", resume)
        return message
    }

    fun saveMessage(message: String) {
        sharedPref.edit().putString(DRAFT_MSG_KEY, message).apply()
    }

    fun saveInfo(
        _name: String?,
        _degree: String?,
        _college: String?,
        _experience: String?,
        _resume: String?
    ) {
        sharedPref.edit().apply {
            if (!_name.isNullOrEmpty() && _name != "<name>") putString(PLACEHOLDER_NAME_KEY, _name)
            if (!_degree.isNullOrEmpty() && _name != "<degree>") putString(
                PLACEHOLDER_DEGREE_KEY,
                _degree
            )
            if (!_college.isNullOrEmpty() && _name != "<college>") putString(
                PLACEHOLDER_COLLEGE_KEY,
                _college
            )
            if (!_experience.isNullOrEmpty() && _name != "<experience>") putString(
                PLACEHOLDER_EXPERIENCE_KEY,
                _experience
            )
            if (!_resume.isNullOrEmpty() && _name != "<resume>") putString(
                PLACEHOLDER_RESUME_KEY,
                _resume
            )
        }.apply()

        Firebase.analytics.logEvent("placeholders") {
            param("has_name", if (_name?.equals("<name>") == false) "true" else "false")
            param("has_degree", if (_name?.equals("<degree>") == false) "true" else "false")
            param("has_college", if (_name?.equals("<college>") == false) "true" else "false")
            param("has_experience", if (_name?.equals("<experience>") == false) "true" else "false")
            param("has_resume", if (_name?.equals("<resume>") == false) "true" else "false")
        }
    }
}