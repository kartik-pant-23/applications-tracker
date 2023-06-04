package com.studbudd.application_tracker.feature_applications.ui.draft_message.placeholders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetupPlaceholderViewModel : ViewModel() {

    private val _placeholderItemsList = MutableLiveData<List<ItemPlaceholder>>(emptyList())
    val placeholderItemsList: LiveData<List<ItemPlaceholder>> = _placeholderItemsList

    fun getInitialPlaceholdersList(
        applicationDataMap: Map<String, String>,
        placeholderDataMap: Map<String, String>
    ) {
        val itemsList = mutableListOf<ItemPlaceholder>()
        itemsList.addAll(applicationDataMap.map { (key, value) ->
            ItemPlaceholder(
                key = key,
                value = value,
                type = ItemPlaceholder.APPLICATION_DATA
            )
        })
        itemsList.addAll(placeholderDataMap.map { (key, value) ->
            ItemPlaceholder(
                key = key,
                value = value,
                type = ItemPlaceholder.PLACEHOLDER_DATA
            )
        })
        _placeholderItemsList.postValue(itemsList)
    }

    private fun getValidatedList(): Pair<MutableList<ItemPlaceholder>, Boolean> {
        val itemsList = _placeholderItemsList.value ?: emptyList()
        val existingKeys = mutableSetOf<String>()
        var hasErrors = false
        val validatedItemsList = itemsList.map {
            val keyEmpty = it.key.isEmpty()
            val keyAlreadyExists = existingKeys.contains(it.key)
            if (keyAlreadyExists or keyEmpty) hasErrors = true
            existingKeys.add(it.key)

            val keyErrorMessage = when {
                keyEmpty -> "Key cannot be empty"
                keyAlreadyExists -> "Key already exists"
                else -> null
            }

            it.copy(keyErrorMessage = keyErrorMessage)
        } as MutableList
        return validatedItemsList to hasErrors
    }

    fun addPlaceholder() {
        val (validatedList, hasErrors) = getValidatedList()
        if (hasErrors) {
            _placeholderItemsList.postValue(validatedList)
            return
        }
        validatedList.add(
            ItemPlaceholder(
                key = "",
                value = "",
                type = ItemPlaceholder.PLACEHOLDER_DATA
            )
        )
        _placeholderItemsList.postValue(validatedList)
    }

    fun removePlaceholder(position: Int) {
        val currentList = (_placeholderItemsList.value ?: emptyList()) as MutableList
        if ((position < 0) or (position >= currentList.size)) return
        currentList.removeAt(position)
        _placeholderItemsList.postValue(currentList)
    }

    fun getUpdatedPlaceholderData(): Pair<Map<String, String>, Boolean> {
        val (validatedList, hasErrors) = getValidatedList()
        if (hasErrors) return emptyMap<String, String>() to true

        val updatedList = validatedList
            .filter { it.type == ItemPlaceholder.PLACEHOLDER_DATA }
            .associate { it.key to it.value }

        return updatedList to false
    }

}