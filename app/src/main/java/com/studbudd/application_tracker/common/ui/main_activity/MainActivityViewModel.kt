package com.studbudd.application_tracker.common.ui.main_activity

import android.util.Log
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
import com.studbudd.application_tracker.feature_user.domain.use_cases.GetUserDataUseCase
import com.studbudd.application_tracker.feature_user.domain.use_cases.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _state = MutableLiveData<MainActivityState>(MainActivityState.Loading())
    val state: LiveData<MainActivityState> = _state

    init {
        checkIfUserLoggedIn()
    }

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

    private fun checkIfUserLoggedIn() = viewModelScope.launch {
        userUseCases.getUser()?.let {
            Log.d("User", "{ name: ${it.name}, email: ${it.email} }")
            _state.postValue(MainActivityState.LoggedIn(it))
        } ?: _state.postValue(MainActivityState.LoggedOut())
    }

}