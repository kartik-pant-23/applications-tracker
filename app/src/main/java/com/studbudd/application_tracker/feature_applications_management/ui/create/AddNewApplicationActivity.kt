package com.studbudd.application_tracker.feature_applications_management.ui.create

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.ActivityAddNewApplicationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewApplicationActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddNewApplicationBinding
    private val viewModel by viewModels<AddNewApplicationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ArrayAdapter.createFromResource(
            this,
            R.array.job_status,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner)
            binding.jobStatus.adapter = adapter
        }

        // as soon as the screen comes in focus
        // company name is focused and keyboard comes out
        binding.companyName.requestFocus()

        binding.submitButton.setOnClickListener { addNewApplication() }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun addNewApplication() {
        val errorMessage = "This field is mandatory!"
        binding.apply {
            companyName.error = if (companyName.text.isNullOrBlank()) errorMessage else null
            jobLink.error = if (jobLink.text.isNullOrBlank()) errorMessage else null
            jobRole.error = if (jobRole.text.isNullOrBlank()) errorMessage else null

            if (!companyName.text.isNullOrBlank() && !jobRole.text.isNullOrBlank() && !jobLink.text.isNullOrBlank()) {
                viewModel.addNewApplication(
                    company = companyName.text!!.toString(),
                    role = jobRole.text!!.toString(),
                    jobLink = jobLink.text!!.toString(),
                    notes = notes.text?.toString(),
                    status = jobStatus.selectedItemPosition
                )
                Toast.makeText(
                    this@AddNewApplicationActivity,
                    "Application successfully added!",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

}