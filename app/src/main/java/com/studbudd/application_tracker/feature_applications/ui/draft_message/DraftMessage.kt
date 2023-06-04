package com.studbudd.application_tracker.feature_applications.ui.draft_message

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.studbudd.application_tracker.core.utils.finishWithTransition
import com.studbudd.application_tracker.core.utils.hideKeyboard
import com.studbudd.application_tracker.core.utils.showInfoSnackbar
import com.studbudd.application_tracker.core.utils.showKeyboard
import com.studbudd.application_tracker.databinding.ActivityDraftMessageBinding
import com.studbudd.application_tracker.feature_applications.ui.draft_message.placeholders.SetupPlaceholderModal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DraftMessage : AppCompatActivity() {
    private lateinit var binding: ActivityDraftMessageBinding
    private val viewModel by viewModels<DraftMessageViewModel>()
    private var applicationDataMap = mutableMapOf<String, String>()
    private lateinit var placeholderModal: SetupPlaceholderModal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDraftMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveApplicationsData(intent)
        setUpView()
        attachOnClickListeners()
    }

    private fun setUpView() {
        binding.editDraftMessage.setText(viewModel.getDraftMessage())
        binding.showPreviewSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked) viewModel.switchToPreviewMode()
            else viewModel.switchToEditMode()
        }
        observeStateChanges()
        setUpPlaceholderModal()
    }

    private fun observeStateChanges() {
        viewModel.previewMessage.observe(this) {
            binding.draftMessage.text = it
        }

        viewModel.uiState.observe(this) {
            changeViewAccordingToMode(it.isPreviewMode)
            it.message?.let { message ->
                binding.root.showInfoSnackbar(message)
            }
        }
    }

    private fun setUpPlaceholderModal() {
        viewModel.placeholderDataMap.observe(this) {
            placeholderModal = SetupPlaceholderModal(
                applicationDataMap = applicationDataMap,
                placeholderDataMap = it,
            ) { updatedPlaceholderData ->
                viewModel.updatePlaceholderData(updatedPlaceholderData)
                placeholderModal.dismiss()
            }
        }
    }

    private fun changeViewAccordingToMode(isPreviewMode: Boolean) {
        with(binding) {
            if (isPreviewMode) {
                viewModel.getPreviewMessage(editDraftMessage.text.toString())
            }

            // setting view visibility
            setupPlaceholderButtonContainer.visibility =
                if (isPreviewMode) View.GONE else View.VISIBLE
            editDraftMessage.visibility = if (isPreviewMode) View.GONE else View.VISIBLE
            checkPreviewButton.visibility = if (isPreviewMode) View.GONE else View.VISIBLE
            previewScreenButtonContainer.visibility = if (isPreviewMode) View.VISIBLE else View.GONE
            draftMessage.visibility = if (isPreviewMode) View.VISIBLE else View.GONE

            if (isPreviewMode) {
                editDraftMessage.hideKeyboard()
            } else {
                editDraftMessage.requestFocus()
            }
        }
    }

    private fun attachOnClickListeners() {
        with(binding) {
            backButton.setOnClickListener { finish() }
            editButton.setOnClickListener { showPreviewSwitch.isChecked = false }
            checkPreviewButton.setOnClickListener { showPreviewSwitch.isChecked = true }
            setupPlaceholderButtonContainer.setOnClickListener {
                placeholderModal.show(supportFragmentManager, SetupPlaceholderModal.TAG)
            }
        }
    }

    private fun saveApplicationsData(intent: Intent) {
        applicationDataMap = mutableMapOf()
        intent.extras?.apply {
            getString(INTENT_KEY_COMPANY)?.let { applicationDataMap[INTENT_KEY_COMPANY] = it }
            getString(INTENT_KEY_ROLE)?.let { applicationDataMap[INTENT_KEY_ROLE] = it }
            getString(INTENT_KEY_JOB_LINK)?.let { applicationDataMap[INTENT_KEY_JOB_LINK] = it }
        }
        viewModel.saveApplicationData(applicationDataMap)
    }

    companion object {
        const val INTENT_KEY_COMPANY = "company"
        const val INTENT_KEY_ROLE = "jobRole"
        const val INTENT_KEY_JOB_LINK = "jobLink"
    }

    override fun finish() {
        super.finish()
        this.finishWithTransition()
    }
}