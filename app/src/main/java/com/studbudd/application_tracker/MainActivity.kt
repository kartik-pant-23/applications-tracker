package com.studbudd.application_tracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.studbudd.application_tracker.common.ui.main_activity.MainActivityState
import com.studbudd.application_tracker.common.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.feature_applications_management.ui.home.ApplicationsFragmentDirections
import com.studbudd.application_tracker.feature_user.ui.onboarding.OnboardingActivity
import com.studbudd.application_tracker.workers.NotifyWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    lateinit var gsc: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATES: Int = 7
        const val UPDATE_REQUEST_CODE: Int = 100
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disabling the center of the bottom navigation
        // to allow clicks on the Floating Action Button
        binding.bottomNavigationView.menu.findItem(R.id.menu_placeholder).isEnabled = false

        // making the content invisible in the beginning
        binding.contentScreen.visibility = View.INVISIBLE

        viewModel.state.observe(this) {
            // making the screen visible only if we have a user
            binding.contentScreen.visibility = if (it.user != null) View.VISIBLE else View.INVISIBLE

            // setting up the screen state if it is loading
            binding.root.isClickable = !it.loading
            binding.loaderScreen.apply {
                progressText.text = it.loaderMessage
                root.visibility = if (it.loading) View.VISIBLE else View.GONE
            }

            // if user is logged out, take him to onboarding screen
            if (it is MainActivityState.LoggedOut) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finishAffinity()
            }
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        onNewIntent(intent)

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

    fun signInWithGoogle() {
        // TODO - Implement Google Sign In feature
        // googleSignInLauncher.launch(gsc.signInIntent)
        Toast.makeText(this, "Feature not yet implemented", Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
        viewModel.removeDataFromTables().invokeOnCompletion {
            val user = viewModel.state.value?.user
            if (user == null || user.isAnonymousUser) {
                viewModel.setUserSignedOut()
            } else {
                viewModel.removeGoogleIdTokens()
                gsc.signOut().addOnCompleteListener {
                    viewModel.setUserSignedOut()
                }
            }
        }
    }
}