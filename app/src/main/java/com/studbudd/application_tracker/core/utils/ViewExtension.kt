package com.studbudd.application_tracker.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

private fun getInputMethodManager(view: View): InputMethodManager {
    return view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}

fun View.showKeyboard() {
    if (this.requestFocus()) {
        val imm = getInputMethodManager(this)
        imm.showSoftInput(this, 0)
    }
}

fun View.hideKeyboard() {
    val imm = getInputMethodManager(this)
    imm.hideSoftInputFromWindow(windowToken, 0)
}
