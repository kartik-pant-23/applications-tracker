package com.studbudd.application_tracker.feature_user.ui.onboarding

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel by viewModels<OnboardingViewModel>()

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var gsRequestLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.anonymousSignIn.setOnClickListener { viewModel.signInAnonymously() }

        initializeGoogleSignInDependencies()
        binding.googleSignIn.setOnClickListener { initiateSignInWithGoogle() }

        viewModel.state.observe(this) { state ->
            binding.loaderScreen.progressText.text = state.loaderMessage ?: ""
            binding.loaderScreen.root.visibility = if (state.loading) View.VISIBLE else View.GONE
            enableButtonClicks(!state.loading)
            if (state is OnboardingState.SignInSuccess) {
                Toast.makeText(
                    this,
                    state.snackBarMessage ?: "User authentication successful",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else if (state is OnboardingState.SignInFailure) {
                state.snackBarMessage?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeGoogleSignInDependencies() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_CLIENT_ID)
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        gsRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))
                } else {
                    Log.e(TAG, "result: $result")
                    viewModel.signInFailed("Google Sign In failed!")
                }
            }
    }

    private fun initiateSignInWithGoogle() {
        gsRequestLauncher.launch(gsc.signInIntent)
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                viewModel.signInFailed()
            }
        } catch (e: ApiException) {
            if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                viewModel.signInFailed("Google Sign In interrupted by user!")
            } else {
                Log.e(TAG, "exception: $e")
                viewModel.signInFailed("Some internal error occurred")
            }
        }
    }

    private fun enableButtonClicks(enable: Boolean) {
        binding.googleSignIn.isEnabled = enable
        binding.anonymousSignIn.isEnabled = enable
    }

    companion object {
        const val TAG = "OnboardingActivity"
    }
}