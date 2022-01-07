package com.studbudd.application_tracker

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.studbudd.application_tracker.databinding.ActivityMainBinding
import com.studbudd.application_tracker.fragments.ApplicationsFragmentDirections
import com.studbudd.application_tracker.workers.NotifyWorker

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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