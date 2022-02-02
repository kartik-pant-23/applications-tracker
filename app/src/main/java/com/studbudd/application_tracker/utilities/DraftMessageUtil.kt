package com.studbudd.application_tracker.utilities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class DraftMessageUtil(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    val draftMessage: String = sharedPref.getString(DRAFT_MSG_KEY, DEFAULT_DRAFT_MESSAGE) ?: DEFAULT_DRAFT_MESSAGE

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
        experience = sharedPref.getString(PLACEHOLDER_EXPERIENCE_KEY, "<experience>") ?: "<experience>"
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
            if (!_name.isNullOrEmpty()) putString(PLACEHOLDER_NAME_KEY, _name)
            if (!_degree.isNullOrEmpty()) putString(PLACEHOLDER_DEGREE_KEY, _degree)
            if (!_college.isNullOrEmpty()) putString(PLACEHOLDER_COLLEGE_KEY, _college)
            if (!_experience.isNullOrEmpty()) putString(PLACEHOLDER_EXPERIENCE_KEY, _experience)
            if (!_resume.isNullOrEmpty()) putString(PLACEHOLDER_RESUME_KEY, _resume)
        }.apply()
    }
}