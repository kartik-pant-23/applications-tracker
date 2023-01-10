package com.studbudd.application_tracker.feature_user.data.entity

data class RemoteIdTuple(
    val remoteId: String?
) {

    fun isConnectedWithRemoteDatabase() =
        remoteId != null

}