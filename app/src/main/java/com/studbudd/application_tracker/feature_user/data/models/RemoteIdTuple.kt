package com.studbudd.application_tracker.feature_user.data.models

data class RemoteIdTuple(
    val remoteId: String?
) {

    fun isConnectedWithRemoteDatabase() =
        remoteId != null

}