package com.studbudd.application_tracker.feature_applications.ui.draft_message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.studbudd.application_tracker.databinding.ActivityDraftMessageBinding

class DraftMessage : AppCompatActivity() {
    private lateinit var binding: ActivityDraftMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDraftMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}