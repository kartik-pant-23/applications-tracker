package com.studbudd.application_tracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.FirebaseApp
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.fragments.ApplicationsFragmentDirections
import com.studbudd.application_tracker.workers.NotifyWorker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode != RESULT_OK) showSnackbar("In-app update failed! Go to play store to update");
    }

    companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATES: Int = 7
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        FirebaseApp.initializeApp(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can now send notifications
                showSnackbar("Notification permission not granted, this is required for the app to work properly")
            }
        }
    }


    private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            updateResultLauncher,
            AppUpdateOptions.newBuilder(updateType).build()
        )
    }

    override fun onResume() {
        super.onResume()
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                var updateType = AppUpdateType.FLEXIBLE
                if (appUpdateInfo.isImmediateUpdateAllowed
                    && (appUpdateInfo.updatePriority() >= 4
                            || (appUpdateInfo.clientVersionStalenessDays()
                        ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATES
                            || !appUpdateInfo.isFlexibleUpdateAllowed)
                ) {
                    updateType = AppUpdateType.IMMEDIATE
                }
                startUpdate(appUpdateInfo, updateType)
            }
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