package com.studbudd.application_tracker.feature_applications.ui.details

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.core.utils.loadImageFromUrl
import com.studbudd.application_tracker.core.utils.finishWithTransition
import com.studbudd.application_tracker.databinding.ActivityApplicationDetailsBinding
import com.studbudd.application_tracker.feature_applications.domain.models.ApplicationStatus
import com.studbudd.application_tracker.feature_applications.domain.models.JobApplication
import com.studbudd.application_tracker.feature_applications.ui.create.ApplicationStatusAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ApplicationDetails : AppCompatActivity() {
    private lateinit var binding: ActivityApplicationDetailsBinding
    private val viewModel by viewModels<ApplicationDetailsViewModel>()

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getLongExtra(EXTRAS_APPLICATION_ID, -1L).apply {
            viewModel.fetchApplicationDetails(this)
        }

        setUpView()
        attachOnClickListeners()
    }

    private fun setUpView() {
        this.lifecycleScope.launch {
            viewModel.availableStatus.collect {
                displayAvailableStatus(it.first, it.second)
            }
        }
        viewModel.application.observe(this) {
            displayApplicationData(it)
        }
        viewModel.uiState.observe(this) {
            isEditMode = it is ApplicationDetailsUiState.EditMode
            changeViewsVisibility(isEditMode)
        }
    }

    private fun displayAvailableStatus(
        data: List<ApplicationStatus>,
        currentStatus: ApplicationStatus
    ) {
        binding.applicationStatusEdit.adapter =
            ApplicationStatusAdapter(this, data)
        binding.applicationStatusEdit.apply {
            setSelection((adapter as ApplicationStatusAdapter).getItemPosition(currentStatus.id))
        }
    }

    private fun displayApplicationData(data: JobApplication) {
        binding.companyName.text = data.job.company
        binding.jobRole.text = data.job.role

        binding.companyLogoText.text = data.job.company.first().toString()
        setupCompanyLogo(data.job.companyLogo)

        binding.applicationStatusDivider.dividerColor = data.status.getColor(this)

        binding.jobLink.apply {
            text = data.job.url
            setLinkTextColor(data.status.getColor(context))
            compoundDrawableTintList = ColorStateList.valueOf(data.status.getColor(context))
        }

        binding.updatedAt.text = "updated ${TimestampHelper.getRelativeTime(data.modifiedAt)}"
        setupApplicationStatus(data.status)

        binding.notes.setLinkTextColor(data.status.getColor(this@ApplicationDetails))
        data.notes?.let {
            binding.notes.text = it
            binding.notes.visibility = View.VISIBLE
            binding.addNotes.visibility = View.GONE
        } ?: run {
            binding.notes.visibility = View.GONE
            binding.addNotes.visibility = View.VISIBLE
        }
        binding.editNotes.setText(data.notes)

        binding.createdAt.text =
            TimestampHelper.getFormattedString(data.createdAt, "dd MMMM, YYYY @ hh:mma")
    }

    private fun changeViewsVisibility(isEditMode: Boolean) {
        binding.editButton.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.sendMessageButton.visibility = if (isEditMode) View.GONE else View.VISIBLE

        binding.applicationStatus.root.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.applicationStatusEditContainer.visibility =
            if (isEditMode) View.VISIBLE else View.GONE

        binding.notesContainer.visibility = if (isEditMode) View.GONE else View.VISIBLE
        binding.editNotes.visibility = if (isEditMode) View.VISIBLE else View.GONE

        binding.saveButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
    }

    private fun setupCompanyLogo(companyLogo: String?) {
        if (companyLogo != null) {
            binding.companyLogoImg.apply {
                loadImageFromUrl(companyLogo, R.drawable.ic_company)
                visibility = View.VISIBLE
            }
            binding.companyLogoText.visibility = View.GONE
        } else {
            binding.companyLogoImg.visibility = View.GONE
            binding.companyLogoText.visibility = View.VISIBLE
        }
    }

    private fun setupApplicationStatus(status: ApplicationStatus) {
        binding.applicationStatus.apply {
            jobStatusTag.apply {
                text = status.tag
                setTextColor(status.getColor(context))
            }
            jobStatusBullet.backgroundTintList =
                ColorStateList.valueOf(status.getColor(this@ApplicationDetails))
        }
    }

    private fun attachOnClickListeners() {
        binding.editButton.setOnClickListener { viewModel.enableEditMode() }
        binding.addNotes.setOnClickListener { viewModel.enableEditMode() }
        binding.backButton.setOnClickListener {
            if (isEditMode) {
                showCloseWithoutSavingAlertDialog()
            } else {
                finish()
            }
        }
    }

    private fun showCloseWithoutSavingAlertDialog() {
        MaterialAlertDialogBuilder(this, R.style.AlertDialog)
            .setTitle("Close Without Saving")
            .setMessage("Are you sure, you want to close the edit mode, without saving your changes?")
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.disableEditMode()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun finish() {
        super.finish()
        this.finishWithTransition()
    }

    companion object {
        const val EXTRAS_APPLICATION_ID = "application_id"
    }
}