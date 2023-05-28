package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.feature_user.data.models.local.UserEntity
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CreateLocalUserUseCase (
    private val repo: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private fun getDefaultPlaceholderMap(name: String, email: String): Map<String, String> {
        return mapOf(
            "name" to name,
            "email" to email,
            "currentRole" to "final year student at XYZ college",
            "yoe" to "2+ years",
            "industry" to "Android App Development",
            "resumeLink" to "https://example.com"
        )
    }

    suspend operator fun invoke() = withContext(dispatcher) {
        // Creating random name and email for anonymous users
        // somewhat adding fun to the app
        val name = getRandomName()
        val email = getEmailFromName(name)
        val createdAt = getCurrentTimestamp()
        val placeholderMap = getDefaultPlaceholderMap(name, email)

        repo.createLocalUser(
            UserEntity(
                name = name,
                email = email,
                createdAt = createdAt,
                placeholderMap = placeholderMap
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