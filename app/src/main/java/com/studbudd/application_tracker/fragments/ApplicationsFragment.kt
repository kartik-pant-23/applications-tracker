package com.studbudd.application_tracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.studbudd.application_tracker.ApplicationsStart
import com.studbudd.application_tracker.adapters.ApplicationAdapter
import com.studbudd.application_tracker.databinding.FragmentApplicationsBinding
import com.studbudd.application_tracker.view_models.ApplicationViewModel

class ApplicationsFragment : Fragment() {

    private var binding: FragmentApplicationsBinding? = null
    lateinit var adapter: ApplicationAdapter
    private val viewModel: ApplicationViewModel by viewModels {
        ApplicationViewModel.ApplicationsViewModelFactory((requireActivity().applicationContext as ApplicationsStart).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplicationsBinding.inflate(inflater, container, false);

        adapter = ApplicationAdapter { itemView, applicationId ->
            val action =
                ApplicationsFragmentDirections.actionApplicationsFragmentToApplicationDetailsFragment(
                    applicationId
                )
            itemView.findNavController().navigate(action)
        }
        viewModel.applicationsList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding?.let {
            val applicationsList = it.rcvScroller.applicationsRcv
            applicationsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            // applicationsList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            applicationsList.adapter = adapter
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}