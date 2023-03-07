package com.studbudd.application_tracker.feature_applications.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.FragmentDraftMessageBinding
import com.studbudd.application_tracker.core.utils.ARG_JOB_LINK
import com.studbudd.application_tracker.core.utils.DraftMessageUtil

class DraftMessageFragment : Fragment() {
    private var jobLink: String? = null
    private var binding: FragmentDraftMessageBinding? = null
    private lateinit var dmUtil: DraftMessageUtil
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jobLink = it.getString(ARG_JOB_LINK)
        }
        dmUtil = DraftMessageUtil()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDraftMessageBinding.inflate(inflater, container, false)

        binding?.apply {
            backButton.setOnClickListener {
                it.findNavController().navigateUp()
            }
            showPreview.setOnCheckedChangeListener { _, checked ->
                showPreviewMessage(checked)
            }
            bottomSheet = BottomSheetBehavior.from(placeholderBottomSheet)
                .apply {
                    addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {}
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            darkBackground.visibility = View.VISIBLE
                            if (slideOffset in 0.0..1.0)  {
                                darkBackground.alpha = slideOffset
                            }
                        }
                    })
                }

            buttonSave.setOnClickListener { savePlaceholdersData() }
            buttonSend.setOnClickListener { sendMessage() }

            // Setting placeholder values
            placeholderName.setText(dmUtil.name)
            placeholderDegree.setText(dmUtil.degree)
            placeholderCollege.setText(dmUtil.college)
            placeholderExperience.setText(dmUtil.experience)
            placeholderResume.setText(dmUtil.resume)

            editDraftMessage.setText(dmUtil.draftMessage)

            showPlaceholderInfo.setOnClickListener {
                if (showPlaceholderInfo.text == getString(R.string.show_placeholder_info)) {
                    placeholderInfoText.visibility = View.VISIBLE
                    showPlaceholderInfo.text = getString(R.string.hide_placeholder_info)
                } else {
                    placeholderInfoText.visibility = View.GONE
                    showPlaceholderInfo.text = getString(R.string.show_placeholder_info)
                }
            }
        }

        return binding?.root
    }

    private fun showPreviewMessage(checked: Boolean) {
        if (binding?.editDraftMessage?.text.isNullOrBlank()) {
            Toast.makeText(this.requireContext(), "No message added!", Toast.LENGTH_SHORT).show()
            binding?.showPreview?.isChecked = false
        } else {
            binding?.editDraftMessage?.visibility = if (checked) View.GONE else View.VISIBLE
            binding?.draftMessageLayout?.visibility = if (checked) View.VISIBLE else View.GONE
            binding?.draftMessage?.text = dmUtil.getPreviewMessage(
                binding?.editDraftMessage?.text.toString(),
                jobLink ?: "~Error~"
            )
        }
    }

    private fun savePlaceholdersData() {
        binding?.apply {
            dmUtil.saveInfo(
                placeholderName.text.toString(),
                placeholderDegree.text.toString(),
                placeholderCollege.text.toString(),
                placeholderExperience.text.toString(),
                placeholderResume.text.toString()
            )
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun sendMessage() {
        if (binding?.editDraftMessage?.text.isNullOrEmpty()) {
            Toast.makeText(this.requireContext(), "Message has no content!", Toast.LENGTH_SHORT).show()
        } else {
            binding?.showPreview?.isChecked = true
            val messageContent = binding?.editDraftMessage?.text.toString()
            val sendMessageIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, dmUtil.getPreviewMessage(messageContent, jobLink ?: "~Error~"))
                type = "text/plain"
            }

            Firebase.analytics.logEvent("send_draft_message") {
                param("draft_msg_length", messageContent.length.toLong())
            }

            dmUtil.saveMessage(messageContent)
            val shareIntent = Intent.createChooser(sendMessageIntent, "Send message via")
            startActivity(shareIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}