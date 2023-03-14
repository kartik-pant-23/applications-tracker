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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.studbudd.application_tracker.BuildConfig
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.R
import com.studbudd.application_tracker.core.utils.startAndFinishAffinity
import com.studbudd.application_tracker.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel by viewModels<OnboardingViewModel>()

    @Inject lateinit var gsc: GoogleSignInClient
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
            binding.root.isClickable = !state.loading
            if (state is OnboardingState.SignInSuccess) {
                Toast.makeText(
                    this,
                    state.snackBarMessage ?: "User authentication successful",
                    Toast.LENGTH_SHORT
                ).show()
                this.startAndFinishAffinity(MainActivity::class.java)
            } else if (state is OnboardingState.SignInFailure) {
                state.snackBarMessage?.let {
                    showSnackbar(it)
                }
            }
        }
    }

    private fun initializeGoogleSignInDependencies() {
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

    /**
     * This function shows a snack bar with the consistent design across the app
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.ANIMATION_MODE_SLIDE
        ).apply {
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        }.show()
    }

    companion object {
        const val TAG = "OnboardingActivity"
    }
}