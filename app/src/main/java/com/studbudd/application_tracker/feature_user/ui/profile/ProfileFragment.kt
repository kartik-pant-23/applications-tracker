package com.studbudd.application_tracker.feature_user.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.common.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.common.ui.views.loadImageFromUrl
import com.studbudd.application_tracker.databinding.FragmentProfileBinding
import com.studbudd.application_tracker.feature_user.ui.placeholder.UpdatePlaceholderActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!
    private val viewModel by activityViewModels<MainActivityViewModel>()

    var isAnonymousUser = true;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewModel.user.observe(viewLifecycleOwner) {
            it?.let { user ->
                binding.userName.valueText = user.name
                binding.userEmail.valueText = user.email
                binding.userJoinedOn.valueText = user.joinedOn
                user.photoUrl?.let { photoUrl ->
                    binding.userImage.loadImageFromUrl(photoUrl, R.drawable.anonymous_user_dp)
                }

                isAnonymousUser = user.isAnonymousUser
                if (user.isAnonymousUser)
                    binding.layoutGoogleSignIn.visibility = View.VISIBLE
                else
                    binding.layoutGoogleSignIn.visibility = View.GONE
            }
        }

        binding.userPlaceholders.setOnClickListener {
            activity?.let {
                startActivity(Intent(it, UpdatePlaceholderActivity::class.java))
                it.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        binding.googleSignIn.setOnClickListener {
            viewModel.startConnectingWithGoogle()
        }

        setupMenu()

        return _binding?.root
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.fragment_profile_menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.toolbar.menu.setGroupDividerEnabled(true)
        } else {
            MenuCompat.setGroupDividerEnabled(binding.toolbar.menu, true)
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            // TODO - Implement menu bar functionality
            when (item.itemId) {
                R.id.about_us -> false
                R.id.privacy_policy -> false
                R.id.contribute -> false
                R.id.sign_out -> {
                    if (isAnonymousUser) showSignInAlertDialog()
                    else showSignOutAlertDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun getAlertDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context, R.style.AlertDialog)
    }

    private fun showSignInAlertDialog() {
        activity?.let {
            getAlertDialog(it)
                .setTitle("Protect your data")
                .setMessage("You have not connected your Google Account, so you will loose your data.")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    showSignOutAlertDialog()
                }
                .setPositiveButton("Connect") { dialog, _ ->
                    viewModel.startConnectingWithGoogle()
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun showSignOutAlertDialog() {
        activity?.let {
            getAlertDialog(it)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from your account?")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Sign Out") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.startLoggingOut()
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}