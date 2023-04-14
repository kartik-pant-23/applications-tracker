package com.studbudd.application_tracker.core.domain

class ParseDraftMessageUseCase {

    operator fun invoke(message: String, specificDataMap: Map<String, String>): String {
        val data = getPlaceholderDataFromDatabase()
        data.putAll(specificDataMap)

        var updatedMessage = message
        for (key in data.keys) {
            updatedMessage = updatedMessage.replace("<$key>", data[key]!!)
        }
        return updatedMessage
    }

    // TODO - complete this implementation
    private fun getPlaceholderDataFromDatabase(): MutableMap<String, String> {
        return mutableMapOf()
    }

}