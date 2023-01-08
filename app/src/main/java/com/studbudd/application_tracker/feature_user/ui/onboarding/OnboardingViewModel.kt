package com.studbudd.application_tracker.feature_user.ui.onboarding

import androidx.lifecycle.*
import com.studbudd.application_tracker.feature_user.domain.use_cases.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _state = MutableLiveData<OnboardingState>()
    val state: LiveData<OnboardingState> = _state

    fun signInWithGoogle(idToken: String) = viewModelScope.launch {
        _state.postValue(OnboardingState.Loading())
        val loginResult = userUseCases.createRemoteUser(idToken)
        if (loginResult.data == true) {
            _state.postValue(OnboardingState.SignInSuccess(loginResult.message))
        } else {
            _state.postValue(OnboardingState.SignInFailure(loginResult.message))
        }
    }

    fun signInAnonymously() = viewModelScope.launch {
        _state.postValue(OnboardingState.Loading("Creating Anonymous User"))
        userUseCases.createLocalUser()
        _state.postValue(OnboardingState.SignInSuccess("Signed In anonymously!"))
    }

    fun signInFailed(message: String? = null) {
        _state.postValue(OnboardingState.SignInFailure(message))
    }

}