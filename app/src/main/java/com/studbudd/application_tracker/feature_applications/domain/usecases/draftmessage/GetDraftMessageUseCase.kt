package com.studbudd.application_tracker.feature_applications.domain.usecases.draftmessage

import com.studbudd.application_tracker.core.utils.SharedPreferencesManager

class GetDraftMessageUseCase(
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    operator fun invoke(): String {
        return sharedPreferencesManager.draftMessage ?: DEFAULT_DRAFT_MESSAGE
    }

    companion object {
        const val DEFAULT_DRAFT_MESSAGE = "Hello,\n" +
                "\n" +
                "I hope this message finds you well. My name is <name> and I am a <currentRole> with <yoe> of experience in <industry>. I am writing to you today to request a referral for a position as <jobRole> at <company>. I am looking for a referral for following job post -\n" +
                "\n" +
                "<jobLink>\n" +
                "\n" +
                "I am confident that my skills and experience are at par with what is required for this role. Along with the technical skills, my team work and leadership qualities add to my personality and make me a perfect fit for the role.\n" +
                "\n" +
                "Following is a link to my resume, please have a look and am looking forward to advices from your side.\n" +
                "\n" +
                "<resumeLink>\n" +
                "\n" +
                "Thank you for considering my request. I look forward to hearing from you soon.\n" +
                "\n" +
                "Best regards,\n" +
                "<name>"
    }
}