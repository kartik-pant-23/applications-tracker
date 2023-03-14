package com.studbudd.application_tracker.feature_applications.ui.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.studbudd.application_tracker.databinding.ActivityApplicationDetailsBinding

class ApplicationDetails : AppCompatActivity() {
    private lateinit var binding: ActivityApplicationDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
        attachOnClickListeners()
    }

    private fun setUpView() {

    }

    private fun attachOnClickListeners() {

    }
}