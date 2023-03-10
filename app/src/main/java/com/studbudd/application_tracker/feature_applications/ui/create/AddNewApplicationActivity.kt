package com.studbudd.application_tracker.feature_applications.ui.create

import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.ui.views.showError
import com.studbudd.application_tracker.core.ui.views.showErrorIfNullOrBlank
import com.studbudd.application_tracker.core.ui.views.showInfoSnackbar
import com.studbudd.application_tracker.databinding.ActivityAddNewApplicationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewApplicationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AddNewApplicationViewModel>()
    lateinit var binding: ActivityAddNewApplicationBinding
    lateinit var adapter: ApplicationStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.companyName.requestFocus()

        binding.submitButton.setOnClickListener { addNewApplication() }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun addNewApplication() {
        binding.apply {
            if (validateInputFields()) {
                viewModel.addNewApplication(
                    company = companyName.text!!.toString(),
                    role = jobRole.text!!.toString(),
                    jobLink = jobLink.text!!.toString(),
                    notes = notes.text?.toString(),
                    status = jobStatus.selectedItemPosition
                )
            }
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
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

}