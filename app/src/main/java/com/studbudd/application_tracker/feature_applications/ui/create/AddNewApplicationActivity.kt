package com.studbudd.application_tracker.feature_applications.ui.create

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.showInfoSnackbar
import com.studbudd.application_tracker.core.utils.finishWithTransition
import com.studbudd.application_tracker.core.utils.showError
import com.studbudd.application_tracker.core.utils.showErrorIfNullOrBlank
import com.studbudd.application_tracker.databinding.ActivityAddNewApplicationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewApplicationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AddNewApplicationViewModel>()
    private lateinit var binding: ActivityAddNewApplicationBinding
    private lateinit var adapter: ApplicationStatusAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    addNewApplication()
                } else {
                    showNotificationsInfoDialog()
                }
            }

        viewModel.applicationStatus.observe(this) {
            adapter = ApplicationStatusAdapter(this, it)
            binding.jobStatus.adapter = adapter
        }

        viewModel.uiState.observe(this) {
            if (it is AddNewApplicationUiState.Info)
                binding.root.showInfoSnackbar(it.message)
            else if (it is AddNewApplicationUiState.Success) {
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.companyName.requestFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(binding.companyName, 0)
        }, KEYBOARD_OPEN_DELAY)

        binding.submitButton.setOnClickListener { handleSubmitButtonClicked(it) }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun handleSubmitButtonClicked(view: View) {
        if (validateInputFields()) {
            when {
                ContextCompat.checkSelfPermission(
                    view.context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> addNewApplication()
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showNotificationsInfoDialog()
                }
                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationsInfoDialog() {
        MaterialAlertDialogBuilder(this, R.style.AlertDialog)
            .apply {
                setIcon(R.drawable.ic_alert_unchecked)
                setTitle(getString(R.string.notifications_alert_title))
                setMessage(getString(R.string.notifications_alert_message))
                setPositiveButton("I'm In") { dialog, _ ->
                    dialog.dismiss()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    addNewApplication()
                }
            }
            .show()
    }

    private fun addNewApplication() {
        binding.apply {
            viewModel.addNewApplication(
                company = companyName.text!!.toString(),
                role = jobRole.text!!.toString(),
                jobLink = jobLink.text!!.toString(),
                notes = notes.text?.toString(),
                status = jobStatus.selectedItemPosition.toLong()
            )
        }
    }

    private fun validateInputFields(): Boolean {
        var isValid: Boolean
        binding.apply {
            companyName.showErrorIfNullOrBlank(
                getString(
                    R.string.empty_field_error_text,
                    "Company Name"
                )
            )
            jobRole.showErrorIfNullOrBlank(getString(R.string.empty_field_error_text, "Job Role"))
            jobLink.showErrorIfNullOrBlank(getString(R.string.empty_field_error_text, "Job Link"))

            val isNotValidUrl =
                !jobLink.text.isNullOrBlank() && !URLUtil.isValidUrl(jobLink.text.toString().trim())
            if (isNotValidUrl) {
                jobLink.showError("Invalid URL")
            }

            isValid = !companyName.text.isNullOrBlank() &&
                        !jobRole.text.isNullOrBlank() &&
                        !jobLink.text.isNullOrBlank() &&
                        !isNotValidUrl
        }
        return isValid
    }

    override fun finish() {
        super.finish()
        this.finishWithTransition()
    }

    companion object {
        private const val KEYBOARD_OPEN_DELAY = 500L
    }

}