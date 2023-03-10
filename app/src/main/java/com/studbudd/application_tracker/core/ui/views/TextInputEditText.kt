package com.studbudd.application_tracker.core.ui.views

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.studbudd.application_tracker.R

fun TextInputEditText.showError(text: String) {
    val typeface = Typeface.create(
        ResourcesCompat.getFont(this.context, R.font.montserrat),
        Typeface.NORMAL
    )
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        CustomTypefaceSpan(typeface),
        0,
        spannableString.length,
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    this.error = spannableString
}
fun TextInputEditText.showErrorIfNullOrBlank(text: String) {
    if (this.text.isNullOrBlank()) {
        showError(text)
    }
}

class CustomTypefaceSpan(
    private val typeface: Typeface
): MetricAffectingSpan() {
    override fun updateDrawState(p0: TextPaint?) {
        p0?.typeface = typeface
    }

    override fun updateMeasureState(p0: TextPaint) {
        p0.typeface = typeface
    }
}