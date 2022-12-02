package com.studbudd.application_tracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.studbudd.application_tracker.common.ui.main_activity.MainActivityState
import com.studbudd.application_tracker.common.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.feature_user.ui.onboarding.OnboardingActivity
import com.studbudd.application_tracker.fragments.ApplicationsFragmentDirections
import com.studbudd.application_tracker.workers.NotifyWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private val viewModel by viewModels<MainActivityViewModel>()

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATES: Int = 7
        const val UPDATE_REQUEST_CODE: Int = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.state.observe(this) {
            when (it) {
                is MainActivityState.Loading -> println("loading")
                is MainActivityState.LoggedIn -> {
                    binding.root.visibility = View.VISIBLE
                    // TODO - Implement screens when user logged in
                }
                is MainActivityState.LoggedOut -> {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finishAffinity()
                }
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController

        onNewIntent(intent)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.applicationsFragment) {
                binding.addApplicationButton.visibility = View.VISIBLE
            } else {
                binding.addApplicationButton.visibility = View.GONE
            }
        }

        binding.addApplicationButton.setOnClickListener {
            navController.navigate(R.id.action_applicationsFragment_to_addApplicationFragment)
        }
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            updateType,
            this,
            UPDATE_REQUEST_CODE
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkForUpdates(appUpdateManager) { appUpdateInfo, updateType ->
            startUpdate(appUpdateInfo, updateType)
        }
    }

    override fun onStart() {
        super.onStart()
        println("on start function")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) showSnackbar("In-app update failed!");
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val applicationId: Int = intent.getIntExtra(NotifyWorker.applicationIdKey, -1)
            if (applicationId != -1) {
                navController.navigate(
                    ApplicationsFragmentDirections.actionApplicationsFragmentToApplicationDetailsFragment(
                        applicationId
                    )
                )
            }
        }
    }
}