package com.studbudd.application_tracker.common.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.InformationTextViewBinding

class InformationTextView (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    private var binding: InformationTextViewBinding
    var keyText = ""
        set(value) {
            field = value
            binding.key.text = field
            binding.key.maxLines = 1
            postInvalidate()
        }
    var valueText = ""
        set(value) {
            field = value
            binding.value.text = field
            binding.value.maxLines = 1
            postInvalidate()
        }
    var endIconVisibility = true
        set(value) {
            field = value
            binding.drawableEnd.visibility = if (field) View.VISIBLE else View.GONE
            postInvalidate()
        }

    init {
        inflate(context, R.layout.information_text_view, this).apply {
            binding = InformationTextViewBinding.bind(this)
        }
        context.obtainStyledAttributes(attrs, R.styleable.InformationTextView).apply {
            try {
                keyText = getString(R.styleable.InformationTextView_keyText) ?: ""
                valueText = getString(R.styleable.InformationTextView_valueText) ?: ""
                endIconVisibility = getBoolean(R.styleable.InformationTextView_endIconVisibility, false)
            } finally {
                recycle()
            }
        }
        binding.value.isSelected = true
    }

}
