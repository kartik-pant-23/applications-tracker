package com.studbudd.application_tracker.core.domain

class GetDraftMessageUseCase(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    operator fun invoke(): String {
        return sharedPreferencesManager.draftMessage ?: DEFAULT_DRAFT_MESSAGE
    }

    companion object {
        const val DEFAULT_DRAFT_MESSAGE =
"""Hi!
I am <name>, a final-year student from <college>. I am interested in an opening at <company> and am looking for a referral

<job-link>

The job seems to align with my interests and expertise. <experience>

I am attaching my resume so that you can know more about my achievements and knowledge.
Resume Link- <resume>

Your referral can help me kickstart my career, kindly look into it.            
Thanks and regards."""
    }
}