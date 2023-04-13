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
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.studbudd.application_tracker.core.ui.main_activity.MainActivityState
import com.studbudd.application_tracker.core.ui.main_activity.MainActivityViewModel
import com.studbudd.application_tracker.core.utils.showInfoSnackbar
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.feature_applications.ui.create.AddNewApplicationActivity
import com.studbudd.application_tracker.feature_user.ui.onboarding.OnboardingActivity
import com.studbudd.application_tracker.core.utils.start
import com.studbudd.application_tracker.core.utils.startAndFinishAffinity
import com.studbudd.application_tracker.feature_applications.data.workers.PeriodicNotificationWorker
import com.studbudd.application_tracker.feature_applications.ui.details.ApplicationDetails
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var installStateUpdateListener: InstallStateUpdatedListener
    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    lateinit var gsc: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    private var isAnonymousUser = false

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATES: Int = 7
        const val UPDATE_REQUEST_CODE: Int = 100
        const val NOTIFICATION_REQUEST_CODE = 101

        const val BOTTOM_NAVIGATION_EXPLORE_TAB = R.id.exploreFragment
        const val BOTTOM_NAVIGATION_ALERTS_TAB = R.id.alertFragment

        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onNewIntent(intent)
        initializeGoogleSignInDependencies()
        initializeAppUpdateDependencies()

        setUpView()
        attachOnClickListener()
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

    private fun initializeAppUpdateDependencies() {
        installStateUpdateListener =
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.FAILED) {
                    binding.root.showInfoSnackbar("Oops!! In-app update failed.")
                } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    binding.root.showInfoSnackbar("Enjoy the latest version of the app.")
                }
            }
    }

    private fun setUpView() {
        viewModel.state.observe(this) {
            setUpLoadingUiState(it)
            setUpGoogleSignInUiState(it)
            setUpLoggingOutUiState(it)
            setUpInfoUiState(it)
        }

        viewModel.user.observe(this) {
            isAnonymousUser = it?.isAnonymousUser == true
        }

        setUpBottomNavigationBar()
    }

    private fun setUpLoadingUiState(uiState: MainActivityState) {
        binding.loaderScreen.apply {
            progressText.text = uiState.loaderMessage
            root.visibility = if (uiState.loading) View.VISIBLE else View.GONE
        }
    }

    private fun setUpGoogleSignInUiState(uiState: MainActivityState) {
        if (uiState is MainActivityState.StartConnectingWithGoogle)
            signInWithGoogle()
        if (uiState is MainActivityState.ConnectedWithGoogle) {
            this.startAndFinishAffinity(MainActivity::class.java)
        }
    }

    private fun setUpLoggingOutUiState(uiState: MainActivityState) {
        if (uiState is MainActivityState.StartLoggingOut)
            signOut()
        if (uiState is MainActivityState.LoggedOut) {
            this.start(OnboardingActivity::class.java)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            finishAffinity()
        }
    }

    private fun setUpInfoUiState(uiState: MainActivityState) {
        if (uiState is MainActivityState.Info) {
            binding.root.showInfoSnackbar(uiState.errorMessage ?: "")
        }
    }

    private fun setUpBottomNavigationBar() {
        attachNavControllerToBottomNavigation()
        binding.bottomNavigationView.menu.findItem(R.id.menu_placeholder).isEnabled = false
        setVisibilityOfBottomNavigationItems()
    }

    private fun attachNavControllerToBottomNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setVisibilityOfBottomNavigationItems() {
        binding.bottomNavigationView.menu.apply {
            findItem(BOTTOM_NAVIGATION_EXPLORE_TAB).isVisible = false
            findItem(BOTTOM_NAVIGATION_ALERTS_TAB).isVisible = false

        }
    }

    private fun attachOnClickListener() {
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
        setUpForInAppUpdate()
        viewModel.checkForUpdates(appUpdateManager) { appUpdateInfo, updateType ->
            startUpdate(appUpdateInfo, updateType)
        }
    }

    private fun setUpForInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateManager.registerListener(installStateUpdateListener)
    }

    override fun onStop() {
        if (this::appUpdateManager.isInitialized) {
            appUpdateManager.unregisterListener(installStateUpdateListener)
        }
    }

    /**
     * This function shows a snack bar with the consistent design for
     * any kind of error that occurs on the `MainActivity`.
     */
//    private fun showSnackbar(message: String) {
//        Snackbar.make(
//            binding.root, message, Snackbar.ANIMATION_MODE_SLIDE
//        ).apply {
//            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
//        }.show()
//    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
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