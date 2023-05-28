package com.studbudd.application_tracker.feature_applications.domain.usecases.draftmessage

import com.studbudd.application_tracker.core.data.models.Resource

class ParseDraftMessageUseCase {

    private fun getMessageAfterModifyingPlaceholders(
        message: String,
        dataMap: Map<String, String>
    ): String {
        var updatedMessage = message
        dataMap.map { (key, value) ->
            updatedMessage = updatedMessage.replace("<$key>", value)
        }
        return updatedMessage
    }

    operator fun invoke(
        message: String,
        applicationSpecificDataMap: Map<String, String>,
        placeholderDataMap: Map<String, String>,
    ): Resource<String> {
        val updatedMessageWithApplicationSpecificData = getMessageAfterModifyingPlaceholders(
            message = message,
            dataMap = applicationSpecificDataMap
        )

        val updatedMessage = getMessageAfterModifyingPlaceholders(
            message = updatedMessageWithApplicationSpecificData,
            dataMap = placeholderDataMap
        )

        return Resource.Success(data = updatedMessage)
    }

}