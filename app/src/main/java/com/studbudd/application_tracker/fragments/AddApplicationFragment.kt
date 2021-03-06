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
import com.studbudd.application_tracker.BaseApplication
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.data.Application
import com.studbudd.application_tracker.databinding.FragmentAddApplicationBinding
import com.studbudd.application_tracker.view_models.ApplicationViewModel

class AddApplicationFragment : Fragment() {

    private val ErrorMessage = "Field cannot be empty!"
    private var binding: FragmentAddApplicationBinding? = null
    private val viewModel: ApplicationViewModel by viewModels {
        ApplicationViewModel.ApplicationsViewModelFactory(
            requireActivity().application,
            (requireActivity().applicationContext as BaseApplication).repository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddApplicationBinding.inflate(inflater, container, false)

        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.job_status,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner)
            binding?.jobStatus?.adapter = adapter
        }

        binding?.backButton?.setOnClickListener{
            it.findNavController().navigateUp()
        }

        binding?.submitButton?.setOnClickListener { addNewApplication(it) }

        return binding?.root
    }

    private fun addNewApplication(view: View) {
        binding?.apply {
            companyName.error = if (companyName.text.isNullOrBlank()) ErrorMessage else null
            jobLink.error = if (jobLink.text.isNullOrBlank()) ErrorMessage else null
            jobRole.error = if (jobRole.text.isNullOrBlank()) ErrorMessage else null

            if (!companyName.text.isNullOrBlank() && !jobRole.text.isNullOrBlank() && !jobLink.text.isNullOrBlank()) {
                val application = Application(
                    company_name = companyName.text!!.toString(),
                    role = jobRole.text!!.toString(),
                    notes = notes.text?.toString(),
                    jobLink = jobLink.text!!.toString(),
                    status = jobStatus.selectedItemPosition
                )
                viewModel.insertApplication(application)
                Toast.makeText(this@AddApplicationFragment.requireContext(), "Application successfully added!", Toast.LENGTH_LONG).show()
                view.findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
