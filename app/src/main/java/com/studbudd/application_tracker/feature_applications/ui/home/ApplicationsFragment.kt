package com.studbudd.application_tracker.feature_applications.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.showInfoSnackbar
import com.studbudd.application_tracker.core.utils.start
import com.studbudd.application_tracker.databinding.FragmentApplicationsBinding
import com.studbudd.application_tracker.feature_applications.ui.details.ApplicationDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplicationsFragment : Fragment() {

    private var _binding: FragmentApplicationsBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<ApplicationsViewModel>()

    private lateinit var adapter: ApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicationsBinding.inflate(inflater)
        setUpViews()
        return _binding?.root
    }

    private fun setUpViews() {
        adapter = ApplicationsAdapter { _, applicationId ->
            (requireActivity() as MainActivity).start(ApplicationDetails::class.java) {
                putExtra(ApplicationDetails.EXTRAS_APPLICATION_ID, applicationId)
            }
        }

        val layoutManager =
            LinearLayoutManager(binding.applicationsList.context, RecyclerView.VERTICAL, false)
        val itemDecoration =
            DividerItemDecoration(binding.applicationsList.context, layoutManager.orientation)
        requireActivity().getDrawable(R.drawable.application_list_divider)?.let {
            itemDecoration.setDrawable(it)
        }
        binding.applicationsList.apply {
            setLayoutManager(layoutManager)
//            addItemDecoration(itemDecoration)
        }.adapter = adapter

        viewModel.listItems.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noApplicationsLayout.root.visibility = View.VISIBLE
                binding.applicationsList.visibility = View.GONE
            } else {
                adapter.submitList(it)
                binding.noApplicationsLayout.root.visibility = View.GONE
                binding.applicationsList.visibility = View.VISIBLE
            }
        }

        binding.noApplicationsLayout.addApplicationButton.setOnClickListener {
            (requireActivity() as MainActivity).openAddNewApplicationActivity()
        }

        binding.filter.setOnClickListener {
            it.showInfoSnackbar("Feature not yet implemented")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}