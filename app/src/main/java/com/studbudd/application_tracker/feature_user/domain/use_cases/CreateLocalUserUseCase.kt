package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CreateLocalUserUseCase @Inject constructor(
    private val repo: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        // Creating random name and email for anonymous users
        // somewhat adding fun to the app
        val name = getRandomName()
        val email = getEmailFromName(name)
        val createdAt = getCurrentTimestamp()

        repo.createLocalUser(
            UserEntity(
                name = name,
                email = email,
                createdAt = createdAt
            )
        )
    }

    private fun getRandomName(): String {
        val firstNames = listOf("Awesome", "Powerful", "Diligent", "Smart")
        val lastNames =
            listOf("Coder", "Developer", "Orator", "Designer", "Analyst", "Engineer")

        val firstName = firstNames.random()
        val lastName = lastNames.random()
        return "$firstName $lastName"
    }

    private fun getEmailFromName(name: String): String {
        val nameParts = name.split(' ')
        val userName = nameParts.first().lowercase()
        val domain = nameParts.last().lowercase()
        return "$userName@$domain.com"
    }

    private fun getCurrentTimestamp(): String {
        val currentTime = Calendar.getInstance().time
        val createdAtDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        return createdAtDateFormat.format(currentTime)
    }
}