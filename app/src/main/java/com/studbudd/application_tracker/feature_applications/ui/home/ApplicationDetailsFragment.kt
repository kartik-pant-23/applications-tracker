package com.studbudd.application_tracker.feature_applications.ui.home

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
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.feature_applications.data.models.local.JobApplicationEntity_Old
import com.studbudd.application_tracker.databinding.FragmentApplicationDetailsBinding
import com.studbudd.application_tracker.core.utils.ARG_APPLICATION_ID
import com.studbudd.application_tracker.core.utils.TimestampHelper
import com.studbudd.application_tracker.view_models.ApplicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ApplicationDetailsFragment : Fragment() {

    private var binding: FragmentApplicationDetailsBinding? = null
    private val viewModel by viewModels<ApplicationViewModel>()
    private var applicationId: Int = 1
    private lateinit var _Job_application: JobApplicationEntity_Old

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

        viewModel.getApplication(applicationId).observe(viewLifecycleOwner) { application ->
            if (application == null) {
                binding?.root?.findNavController()?.navigateUp()
            } else {
                _Job_application = JobApplicationEntity_Old(
                    companyName = application.application.company,
                    role = application.application.role,
                    jobLink = application.application.jobLink,
                    status = application.status.id,
                    notes = application.application.notes,
                    companyLogo = application.application.companyLogo,
                    createdAtCalendar = Calendar.getInstance(),
                    modifiedAtCalendar = Calendar.getInstance(),
                )
                binding?.run {
                    applicationTitle.text = _Job_application.title
                    notes.text =
                        if (_Job_application.notes.isNullOrBlank())
                            getString(R.string.placeholder_null_notes)
                        else _Job_application.notes
                    editNotes.setText(_Job_application.notes)

                    jobLink.text = _Job_application.jobLink
                    editJobLink.setText(_Job_application.jobLink)

                    applicationCreatedAt.text =
                        TimestampHelper.getFormattedString(_Job_application.createdAt ?: "", TimestampHelper.DETAILED)
                    jobStatus.text = application.status.tag

                    ArrayAdapter.createFromResource(
                        this@ApplicationDetailsFragment.requireContext(),
                        R.array.job_status,
                        R.layout.item_spinner
                    ).also { adapter ->
                        adapter.setDropDownViewResource(R.layout.item_spinner)
                        editJobStatus.adapter = adapter
                    }
                    editJobStatus.setSelection(_Job_application.status.toInt(), true)
                }
            }
        }

        viewModel.editMode.observe(viewLifecycleOwner) {
            if (it != null) showEditMode(it)
        }

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
                viewModel.deleteApplication(_Job_application).invokeOnCompletion {
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

    private fun sendMessage(view: View) {
        val action = ApplicationDetailsFragmentDirections
            .actionApplicationDetailsFragmentToDraftMessageFragment(_Job_application.jobLink)
        view.findNavController().navigate(action)
    }

    private fun saveChanges() {
        binding?.run {
            if (editJobLink.text.isNullOrBlank()) {
                editJobLink.error = "Field cannot be empty!"
            } else {
                viewModel.updateApplication(
                    JobApplicationEntity_Old(
                    companyName = _Job_application.companyName,
                    role = _Job_application.role,
                    jobLink = editJobLink.text.toString(),
                    notes = editNotes.text.toString(),
                    status = editJobStatus.selectedItemPosition.toLong()
                )
                )
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