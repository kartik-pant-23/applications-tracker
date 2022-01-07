package com.studbudd.application_tracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studbudd.application_tracker.BaseApplication
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.databinding.FragmentApplicationDetailsBinding
import com.studbudd.application_tracker.utilities.ARG_APPLICATION_ID
import com.studbudd.application_tracker.utilities.DATE_FORMAT
import com.studbudd.application_tracker.view_models.ApplicationViewModel

class ApplicationDetailsFragment : Fragment() {

    private var binding: FragmentApplicationDetailsBinding? = null
    private val viewModel: ApplicationViewModel by viewModels {
        ApplicationViewModel.ApplicationsViewModelFactory(
            requireActivity().application,
            (requireActivity().applicationContext as BaseApplication).repository
        )
    }
    private var applicationId: Int = 1
    private lateinit var _application: Application

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            applicationId = it.getInt(ARG_APPLICATION_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplicationDetailsBinding.inflate(inflater, container, false)

        viewModel.getApplication(applicationId).observe(viewLifecycleOwner, { application ->
            if (application == null) {
                binding?.root?.findNavController()?.navigateUp()
            } else {
                _application = application
                binding?.run {
                    applicationTitle.text = application.title
                    notes.text =
                        if (application.notes.isNullOrBlank()) getString(R.string.placeholder_null_notes) else application.notes
                    editNotes.setText(application.notes)

                    jobLink.text = application.jobLink
                    editJobLink.setText(application.jobLink)

                    applicationCreatedAt.text =
                        DATE_FORMAT.format(application.created_at.timeInMillis).toString()
                    jobStatus.text =
                        resources.getStringArray(R.array.job_status)[application.status]

                    ArrayAdapter.createFromResource(
                        this@ApplicationDetailsFragment.requireContext(),
                        R.array.job_status,
                        R.layout.item_spinner
                    ).also { adapter ->
                        adapter.setDropDownViewResource(R.layout.item_spinner)
                        editJobStatus.adapter = adapter
                    }
                    editJobStatus.setSelection(application.status, true)
                }
            }
        })

        viewModel.editMode.observe(viewLifecycleOwner, {
            if (it != null) showEditMode(it)
        })

        // On click listeners for navigation bar buttons
        binding?.run {
            backButton.setOnClickListener{ it.findNavController().navigateUp() }

            buttonDelete.setOnClickListener{ deleteApplication(it) }
            buttonSend.setOnClickListener{ sendMessage(it) }
            buttonEdit.setOnClickListener {
                viewModel.changeEditMode(true)
            }
            buttonSave.setOnClickListener { saveChanges() }
        }

        return binding?.root
    }

    private fun visibility(editMode: Boolean, type: Int): Int {
        return if (editMode) {
            if (type == 0) View.VISIBLE else View.GONE
        } else {
            if (type == 0) View.GONE else View.VISIBLE
        }
    }
    private fun showEditMode(editMode: Boolean) {
        binding?.run {
            notesLayout.visibility = visibility(editMode, 1)
            editNotesLayout.visibility = visibility(editMode, 0)

            jobLinkLayout.visibility = visibility(editMode, 1)
            editJobLinkLayout.visibility = visibility(editMode, 0)

            jobStatusLayout.visibility = visibility(editMode, 1)
            editJobStatusLayout.visibility = visibility(editMode, 0)

            updateLayout.visibility = visibility(editMode, 1)
            buttonSave.visibility = visibility(editMode, 0)
        }
    }

    private fun deleteApplication(view: View) {
        MaterialAlertDialogBuilder(view.context, R.style.CustomAlertDialog)
            .setTitle("Discard application?")
            .setMessage("This application will be deleted and can no longer be recovered.")
            .setPositiveButton("Discard") { _, _ ->
                viewModel.deleteApplication(_application).invokeOnCompletion {
                    Toast.makeText(this.requireContext(), "Application deleted!", Toast.LENGTH_LONG)
                        .show()
                    view.findNavController().navigateUp()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendMessage(view: View) {}

    private fun saveChanges() {
        binding?.run {
            if (editJobLink.text.isNullOrBlank()) {
                editJobLink.error = "Field cannot be empty!"
            } else {
                _application.notes = editNotes.text.toString()
                _application.jobLink = editJobLink.text.toString()
                _application.status = editJobStatus.selectedItemPosition
                viewModel.updateApplication(_application)
                viewModel.changeEditMode(false)
                Toast.makeText(this@ApplicationDetailsFragment.requireContext(), "Changes saved!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}