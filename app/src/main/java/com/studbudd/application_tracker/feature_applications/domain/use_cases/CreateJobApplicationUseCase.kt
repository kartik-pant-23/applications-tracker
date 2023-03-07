package com.studbudd.application_tracker.feature_applications.domain.use_cases

import android.webkit.URLUtil
import com.studbudd.application_tracker.core.data.models.Resource
import com.studbudd.application_tracker.feature_applications.data.repo.JobApplicationsRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class CreateJobApplicationUseCase (
    private val repo: JobApplicationsRepository,
) {

    /**
     * - Trims all the strings, and if any of these is blank, then return Error
     * - Make sure that [url] is a valid link.
     *
     * *If somehow we can check that this url is actually a jobLink for the
     * mentioned [company].*
     */
    suspend operator fun invoke(
        company: String,
        role: String,
        url: String,
        notes: String?,
        status: Int
    ): Resource<Unit> {
        println(URLUtil.guessUrl(url))
        return if (company.isBlank() || role.isBlank() || url.isBlank())
            Resource.Failure("Some mandatory fields were empty..")
        else if (!URLUtil.isValidUrl(url.trim())) {
            Resource.Failure("Invalid job link added..")
        } else {
            repo.create(
                company = company.trim(),
                role = role.trim(),
                jobUrl = url.trim(),
                notes = notes,
                status = status
            )
        }
    }

}