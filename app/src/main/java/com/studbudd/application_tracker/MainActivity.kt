package com.studbudd.application_tracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.studbudd.application_tracker.core.ui.main_activity.MainActivityState
import com.studbudd.application_tracker.core.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.feature_applications.ui.create.AddNewApplicationActivity
import com.studbudd.application_tracker.feature_applications.ui.home.ApplicationsFragmentDirections
import com.studbudd.application_tracker.feature_user.ui.onboarding.OnboardingActivity
import com.studbudd.application_tracker.core.utils.start
import com.studbudd.application_tracker.feature_applications.data.workers.PeriodicNotificationWorker
import com.studbudd.application_tracker.feature_applications.ui.details.ApplicationDetails
import dagger.hilt.android.AndroidEntryPoint
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

    var isAnonymousUser = false

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATES: Int = 7
        const val UPDATE_REQUEST_CODE: Int = 100
        const val NOTIFICATION_REQUEST_CODE = 101
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeGoogleSignInDependencies()

        // Disabling the center of the bottom navigation
        // to allow clicks on the Floating Action Button
        binding.bottomNavigationView.menu.findItem(R.id.menu_placeholder).isEnabled = false

        viewModel.state.observe(this) {
            // setting up the screen state if it is loading
            binding.loaderScreen.apply {
                progressText.text = it.loaderMessage
                root.visibility = if (it.loading) View.VISIBLE else View.GONE
            }

            // start connecting user's account with google
            if (it is MainActivityState.StartConnectingWithGoogle)
                signInWithGoogle()
            // if the user's google account is connected, recreate the activity
            if (it is MainActivityState.ConnectedWithGoogle) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }

            // start the logging out process
            if (it is MainActivityState.StartLoggingOut)
                signOut()
            // if user is logged out, take him to the onboarding screen
            if (it is MainActivityState.LoggedOut) {
                startActivity(Intent(this, OnboardingActivity::class.java))
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finishAffinity()
            }

            // show the error message
            if (it is MainActivityState.Info) {
                showSnackbar(it.errorMessage ?: "")
            }
        }

        // making the screen visible only if we have a user
        viewModel.user.observe(this) {
            isAnonymousUser = it?.isAnonymousUser == true
        }


        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        onNewIntent(intent)

        binding.addApplicationButton.setOnClickListener { openAddNewApplicationActivity() }
    }

    fun openAddNewApplicationActivity() {
        this.start(AddNewApplicationActivity::class.java)
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

    /**
     * Handles the case when the update is available but gets failed
     * due to some other error.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) viewModel.showError("In-app update failed!");
        }
    }

    /**
     * This function shows a snack bar with the consistent design for
     * any kind of error that occurs on the `MainActivity`.
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.ANIMATION_MODE_SLIDE
        ).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        }.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    /**
     * For handling when the app is opened from the notifications.
     * If it is opened from a notification, the `applicationId` will have
     * a value other than -1.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val applicationId: Long =
                intent.getLongExtra(PeriodicNotificationWorker.JOB_APPLICATION_ID, -1)
            if (applicationId != -1L) {
                /* navController.navigate(
                    ApplicationsFragmentDirections.actionApplicationsFragmentToApplicationDetailsFragment(
                        applicationId
                    )
                ) */
                this.start(ApplicationDetails::class.java) {
                    putExtra(ApplicationDetails.EXTRAS_APPLICATION_ID, applicationId)
                }
            }
        }
    }

    /**
     * Sets up the Google Sign In Launcher - which will handle the users action
     * after the user selects an account from the dialog shown.
     */
    private fun initializeGoogleSignInDependencies() {
        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        val idToken = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            .getResult(ApiException::class.java).idToken
                        if (idToken != null) {
                            viewModel.signInRemoteUser(idToken)
                        } else {
                            viewModel.showError("Did not get token for signing in the user.")
                        }
                    } catch (e: ApiException) {
                        if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                            viewModel.showError("Sign in failed due to user interruption!")
                        } else {
                            viewModel.showError("Google Sign in failed due to some internal error.")
                        }
                    }
                } else {
                    viewModel.showError("Something went wrong.")
                    Log.e(TAG, "google-sign-in-failed: $result")
                }
            }
    }

    /**
     * Launches the Google Sign-In intent - that is the alert dialog
     * for creating an account chooser dialog for the user.
     */
    private fun signInWithGoogle() {
        viewModel.setLoading()
        googleSignInLauncher.launch(gsc.signInIntent)
    }

    /**
     * Signs out the user by the following two steps -
     * - Clear all the tables from the local SQLite database.
     * - If the user has connected their Google Account, then it should
     *   be removed as well. This ensures that the users can choose another
     *   google account at the time of logging.
     */
    private fun signOut() {
        viewModel.removeDataFromTables().invokeOnCompletion {
            if (isAnonymousUser) {
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