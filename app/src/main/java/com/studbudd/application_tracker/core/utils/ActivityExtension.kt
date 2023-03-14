package com.studbudd.application_tracker.core.utils

import android.app.Activity
import android.content.Intent
import androidx.navigation.ActivityNavigator
import com.studbudd.application_tracker.R

fun Activity.start(cls: Class<*>) {
    startActivity(Intent(this, cls))
    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
}

fun Activity.startAndFinish(cls: Class<*>) {
    start(cls)
    finish()
}

fun Activity.startAndFinishAffinity(cls: Class<*>) {
    start(cls)
    finishAffinity()
}

fun Activity.finishWithTransition() {
    ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
}

