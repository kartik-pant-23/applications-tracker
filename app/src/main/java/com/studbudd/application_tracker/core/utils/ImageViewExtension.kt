package com.studbudd.application_tracker.core.utils

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageFromUrl(url: String, errorImageResource: Int? = null) {
    Glide
        .with(this.context)
        .load(url)
        .error(errorImageResource)
        .centerInside()
        .into(this)
}