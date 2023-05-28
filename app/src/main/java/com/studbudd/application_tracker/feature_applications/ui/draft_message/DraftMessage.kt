package com.studbudd.application_tracker.feature_applications.ui.draft_message

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.studbudd.application_tracker.core.utils.finishWithTransition
import com.studbudd.application_tracker.databinding.ActivityDraftMessageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DraftMessage : AppCompatActivity() {
    private lateinit var binding: ActivityDraftMessageBinding

    private val viewModel by viewModels<DraftMessageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDraftMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView(intent)
        attachOnClickListeners()
    }

    private fun setUpView(intent: Intent) {
        binding.editDraftMessage.setText(viewModel.getDraftMessage())
        saveApplicationsData(intent)
        setUpTabLayout()
        setUpPlaceholdersList()
    }

    private fun attachOnClickListeners() {
        binding.backButton.setOnClickListener { finish() }
    }

    private fun saveApplicationsData(intent: Intent) {
        val dataMap = mutableMapOf<String, String>()
        intent.extras?.apply {
            getString(INTENT_KEY_COMPANY)?.let { dataMap[INTENT_KEY_COMPANY] = it }
            getString(INTENT_KEY_ROLE)?.let { dataMap[INTENT_KEY_ROLE] = it }
            getString(INTENT_KEY_JOB_LINK)?.let { dataMap[INTENT_KEY_JOB_LINK] = it }
        }
        viewModel.saveApplicationData(dataMap)
    }

    private fun setUpTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                handleViewChangesOnTabSelect(tab?.position == TAB_PREVIEW)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun handleViewChangesOnTabSelect(isPreviewSelected: Boolean) {
        binding.editDraftMessage.visibility = if (isPreviewSelected) View.GONE else View.VISIBLE
        with(binding.draftMessage) {
            visibility = if (isPreviewSelected) View.VISIBLE else View.GONE
            text = viewModel.getPreviewMessage(binding.editDraftMessage.text.toString())
        }
    }

    private fun setUpPlaceholdersList() {
        val placeholderAdapter = PlaceholderAdapter()
        with(binding.bottomSheet.placeholdersList) {
            layoutManager = LinearLayoutManager(this@DraftMessage, RecyclerView.HORIZONTAL, false)
            adapter = placeholderAdapter
        }

        viewModel.placeholderDataMap.observe(this) {
            placeholderAdapter.submitList(it.toList())
        }
    }

    companion object {
        const val INTENT_KEY_COMPANY = "company"
        const val INTENT_KEY_ROLE = "role"
        const val INTENT_KEY_JOB_LINK = "jobLink"

        const val TAB_DRAFT = 0
        const val TAB_PREVIEW = 1
    }

    override fun finish() {
        super.finish()
        this.finishWithTransition()
    }
}