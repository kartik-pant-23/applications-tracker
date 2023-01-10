package com.studbudd.application_tracker.common.ui.main_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.studbudd.application_tracker.MainActivity
import com.studbudd.application_tracker.common.domain.ClearAppDataUseCase
import com.studbudd.application_tracker.common.data.models.Resource
import com.studbudd.application_tracker.feature_user.domain.models.User
import com.studbudd.application_tracker.feature_user.domain.use_cases.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val clearAppDataUseCase: ClearAppDataUseCase
) : ViewModel() {

    private val _state = MutableLiveData<MainActivityState>(MainActivityState.Loading())
    val state: LiveData<MainActivityState> = _state

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        checkIfUserLoggedIn()
    }

    /**
     * This function checks if there are any pending updates.
     * If the updates are present app updation flow is started.
     */
    fun checkForUpdates(
        appUpdateManager: AppUpdateManager,
        startUpdate: (appUpdateInfo: AppUpdateInfo, updateType: Int) -> Unit
    ) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                var updateType = AppUpdateType.FLEXIBLE
                if (appUpdateInfo.isImmediateUpdateAllowed
                    && (appUpdateInfo.updatePriority() >= 4
                            || (appUpdateInfo.clientVersionStalenessDays()
                        ?: -1) >= MainActivity.DAYS_FOR_FLEXIBLE_UPDATES
                            || !appUpdateInfo.isFlexibleUpdateAllowed)
                ) {
                    updateType = AppUpdateType.IMMEDIATE
                }
                startUpdate(appUpdateInfo, updateType)
            }
        }
    }

    /**
     * This function is called as soon as the user opens up the app.
     * Through this function we try to get the user's data from our
     * **single source of truth**, the local database.
     *
     * If the local database does not have the user's data then we
     * start the logging out flow - which clears up the local
     * database.
     */
    private fun checkIfUserLoggedIn() = viewModelScope.launch {
        _state.postValue(
            MainActivityState.Loading("Loading user details")
        )
        userUseCases.getUser().collect {
            if (it is Resource.LoggedOut) {
                startLoggingOut()
            } else {
                _user.postValue(it.data)
                if (it is Resource.Success)
                    _state.postValue(MainActivityState.Default())
                else
                    _state.postValue(MainActivityState.Info(it.message))
            }
        }
    }

    /**
     * This function marks the starting of connecting the user's account
     * with their google account by setting main activity's state to
     * [MainActivityState.StartConnectingWithGoogle]
     */
    fun startConnectingWithGoogle() =
        _state.postValue(MainActivityState.StartConnectingWithGoogle())

    /**
     * This function connects the user's account with Google account
     * allowing them to save their data over the remote database.
     */
    fun signInRemoteUser(idToken: String) = viewModelScope.launch {
        when (val res = userUseCases.createRemoteUser(idToken)) {
            is Resource.Success -> {
                _state.postValue(MainActivityState.Loading("App will be restarted to make changes"))
                delay(1000)
                _state.postValue(MainActivityState.ConnectedWithGoogle())
            }
            else -> {
                _state.postValue(MainActivityState.Info(res.message))
            }
        }
    }

    /**
     * This function removes all the data stored in the local database, it deletes
     * all the tables from the database, but not the sequences *(this is not
     * required in our case as well)*.
     *
     * Earlier we were showing a message on the loader screen, but that would be somewhat
     * incorrect in the case when the app is opened up for the first time. So removed
     * that loader screen message.
     */
    fun removeDataFromTables() = viewModelScope.launch {
        clearAppDataUseCase()
    }

    /**
     * This function is just to change the loader screen message,
     * actual deletion of google sign-in tokens happen in the
     * activity itself.
     */
    fun removeGoogleIdTokens() {
        _state.postValue(
            MainActivityState.Loading("Deleting Google Sign In credentials from your device")
        )
    }

    /**
     * This function will set the state of main activity to
     * [MainActivityState.Loading]
     */
    fun setLoading(message: String? = null) =
        _state.postValue(MainActivityState.Loading(message))

    /**
     * This function starts the sign out process for the user
     * which includes the following steps -
     * 1. Clearing out all the tables from the local database.
     * 2. Removing the Google Id tokens from the device to allow user
     *    for signing in again from a different account.
     *
     * This function changes the [MainActivityState.StartLoggingOut] to mark that sign out
     * process has started and rest of the functions are called from the
     * [MainActivity] itself.
     */
    fun startLoggingOut() = _state.postValue(MainActivityState.StartLoggingOut())

    /**
     * This function marks the finish of the logging out process
     * by setting the main activity's state a [MainActivityState.LoggedOut].
     */
    fun setUserSignedOut() = _state.postValue(MainActivityState.LoggedOut())

    /**
     * This functions sets an error message so it will trigger showing the snack bar.
     */
    fun showError(message: String? = null) =
        _state.postValue(MainActivityState.Info(message ?: "Something went wrong"))


}