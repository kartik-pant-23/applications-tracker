package com.studbudd.application_tracker.core.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.CircularButtonBinding

class CircularButton(
    context: Context,
    attrs: AttributeSet
) : MaterialCardView(context, attrs) {

    private val binding: CircularButtonBinding

    private var buttonBackgroundColor = context.getColor(R.color.card_color)
        set(value) {
            field = value
            binding.buttonIcon.setBackgroundColor(field)
            postInvalidate()
        }

    private var buttonElevation = 0f
        set(value) {
            field = value
            binding.buttonContainer.cardElevation = field
            postInvalidate()
        }

    private var buttonIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back)
        set(value) {
            field = value
            binding.buttonIcon.setImageDrawable(field)
            postInvalidate()
        }

    private var buttonIconTint = ContextCompat.getColor(context, R.color.dark_text)
        set(value) {
            field = value
            binding.buttonIcon.imageTintList = ColorStateList.valueOf(field)
            postInvalidate()
        }

    init {
        inflate(context, R.layout.circular_button, this).apply {
            binding = CircularButtonBinding.bind(this)
        }

        context.obtainStyledAttributes(attrs, R.styleable.CircularButton).apply {
            try {
                buttonBackgroundColor = getColor(
                    R.styleable.CircularButton_buttonBackgroundColor,
                    ContextCompat.getColor(context, R.color.card_color)
                )

                buttonElevation = getDimension(
                    R.styleable.CircularButton_buttonElevation,
                    0f
                )

                buttonIcon = getDrawable(R.styleable.CircularButton_buttonIcon)
                    ?: ContextCompat.getDrawable(context, R.drawable.ic_arrow_back)

                buttonIconTint = getColor(
                    R.styleable.CircularButton_buttonIconTint,
                    ContextCompat.getColor(context, R.color.dark_text)
                )

                binding.buttonContainer.radius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics)
            } finally {
                recycle()
            }
        }
    }

}