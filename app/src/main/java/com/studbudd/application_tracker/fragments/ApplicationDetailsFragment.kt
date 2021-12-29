package com.studbudd.application_tracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import com.studbudd.application_tracker.ApplicationsStart
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.FragmentApplicationDetailsBinding
import com.studbudd.application_tracker.utilities.ARG_APPLICATION_ID
import com.studbudd.application_tracker.utilities.DATE_FORMAT
import com.studbudd.application_tracker.view_models.ApplicationViewModel

class ApplicationDetailsFragment : Fragment() {

    private var binding: FragmentApplicationDetailsBinding? = null
    private val viewModel: ApplicationViewModel by viewModels {
        ApplicationViewModel.ApplicationsViewModelFactory((requireActivity().applicationContext as ApplicationsStart).repository)
    }
    private var applicationId: Int = 1

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
            binding?.run {
                applicationTitle.text = application?.title
                notes.text = if (application.notes.isNullOrBlank()) getString(R.string.placeholder_null_notes) else application.notes
                jobLink.text = application.jobLink
                applicationCreatedAt.text = DATE_FORMAT.format(application.created_at.timeInMillis).toString()
                jobStatus.text = resources.getStringArray(R.array.job_status)[application.status]
            }
        })

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}