package com.studbudd.application_tracker.core.ui.views

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun View.showInfoSnackbar(message: String) {
    Snackbar.make(
       this, message, Snackbar.ANIMATION_MODE_SLIDE
    ).apply {
        animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
    }.show()
}