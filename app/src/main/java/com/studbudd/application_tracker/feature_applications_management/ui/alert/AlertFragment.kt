package com.studbudd.application_tracker.feature_applications_management.ui.alert

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.studbudd.application_tracker.databinding.FragmentAlertBinding

class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertBinding.inflate(inflater, container, false)



        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AlertFragment()
    }
}