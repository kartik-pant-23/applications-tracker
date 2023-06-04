package com.studbudd.application_tracker.feature_applications.ui.draft_message.placeholders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.BottomSheetSetupPlaceholdersBinding

class SetupPlaceholderModal(
    private val applicationDataMap: Map<String, String>,
    private val placeholderDataMap: Map<String, String>,
    private val updatePlaceholderData: (updatedPlaceholderData: Map<String, String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSetupPlaceholdersBinding

    private val viewModel by viewModels<SetupPlaceholderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSetupPlaceholdersBinding.inflate(inflater, container, false)
        viewModel.getInitialPlaceholdersList(applicationDataMap, placeholderDataMap)
        setupView()
        attachOnClickListeners()
        return binding.root
    }

    private fun setupView() {
        setPlaceholderListView()
        observeStateChanges()
    }

    private fun setPlaceholderListView() {
        with(binding.placeholdersRcv) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = PlaceholderAdapter() { viewModel.removePlaceholder(it) }
        }
    }

    private fun observeStateChanges() {
        viewModel.placeholderItemsList.observe(this) {
            binding.placeholdersRcv.adapter?.let { adapter ->
                (adapter as PlaceholderAdapter).submitList(it)
            }
        }
    }

    private fun attachOnClickListeners() {
        with(binding) {
            addPlaceholder.setOnClickListener { viewModel.addPlaceholder() }
            updatePlaceholders.setOnClickListener {
                val (updatedPlaceholderData, hasErrors) = viewModel.getUpdatedPlaceholderData()
                if (hasErrors) {
                    context?.let { context ->
                        Toast.makeText(
                            context,
                            "Cannot save placeholder data with errors.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@setOnClickListener
                }
                updatePlaceholderData(updatedPlaceholderData)
            }
        }
    }

    companion object {
        const val TAG = "SETUP_PLACEHOLDER_MODAL"
    }

}