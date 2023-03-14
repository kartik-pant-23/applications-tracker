package com.studbudd.application_tracker.feature_applications.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.core.utils.showInfoSnackbar
import com.studbudd.application_tracker.databinding.FragmentApplicationsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplicationsFragment : Fragment() {

    private var _binding: FragmentApplicationsBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel by viewModels<ApplicationsViewModel>()
    private val activityViewModel by activityViewModels<MainActivityViewModel>()

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
        adapter = ApplicationsAdapter { item, applicationId ->
            item.findNavController().navigate(
                ApplicationsFragmentDirections.actionApplicationsFragmentToApplicationDetailsFragment(
                    applicationId
                )
            )
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
            addItemDecoration(itemDecoration)
        }.adapter = adapter

        viewModel.applicationsList.observe(viewLifecycleOwner) {
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