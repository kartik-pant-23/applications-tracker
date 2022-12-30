package com.studbudd.application_tracker.feature_user.ui.placeholder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.ActivityNavigator
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.ActivityUpdatePlaceholderBinding

class UpdatePlaceholderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePlaceholderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}