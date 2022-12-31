package com.studbudd.application_tracker.feature_user.domain.use_cases

import com.studbudd.application_tracker.common.domain.SharedPreferencesManager
import com.studbudd.application_tracker.feature_user.data.models.UserLocal
import com.studbudd.application_tracker.feature_user.data.repo.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AnonymousSignInUseCase @Inject constructor(
    private val preferencesManager: SharedPreferencesManager,
    private val repo: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val firstNames = listOf("Awesome", "Powerful", "Diligent", "Smart")
    private val lastNames =
        listOf("Coder", "Developer", "Orator", "Designer", "Analyst", "Engineer")

    suspend operator fun invoke() = withContext(dispatcher) {
        // Creating random names for anonymous users
        // somewhat adding fun to the app
        val firstName = firstNames.random()
        val lastName = lastNames.random()
        val name = "$firstName $lastName"
        val email = "${firstName.lowercase()}@${lastName.lowercase()}.com"

        // adding created at date
        val dateToday = Calendar.getInstance().time
        val createdAtDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)

        // creating a new user with the above data
        repo.createAnonymousUser(
            UserLocal(
                name = name,
                email = email,
                createdAt = createdAtDateFormat.format(dateToday)
            )
        )
        preferencesManager.refreshToken = "LOCAL"
    }
}