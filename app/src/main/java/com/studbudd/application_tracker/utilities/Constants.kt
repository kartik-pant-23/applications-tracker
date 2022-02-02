package com.studbudd.application_tracker.utilities

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

const val DATABASE_NAME = "applications_tracker_database"

const val ARG_APPLICATION_ID = "application_id"
const val ARG_JOB_LINK = "job_link"

@SuppressLint("SimpleDateFormat")
val DATE_FORMAT = SimpleDateFormat("d MMM yyyy, hh:mm a")

const val SHARED_PREFERENCES_KEY = "ufoa43a198ac3nkhd27acad23nkdncq"
const val DRAFT_MSG_KEY = "DRAFT_MSG_KEY"
const val PLACEHOLDER_NAME_KEY = "NAME_KEY"
const val PLACEHOLDER_DEGREE_KEY = "DEGREE_KEY"
const val PLACEHOLDER_COLLEGE_KEY = "COLLEGE_KEY"
const val PLACEHOLDER_EXPERIENCE_KEY = "EXPERIENCE_KEY"
const val PLACEHOLDER_RESUME_KEY = "RESUME_KEY"

const val DEFAULT_DRAFT_MESSAGE = "Hi there! I am <name> currently pursuing <degree> from <college>. I have <experience>.\n\nCurrently I am looking for a referral for following job - \n\n<job-link>\n\nYou can find my resume here <resume>. Hoping for a positive response from your side. Thank You!!"