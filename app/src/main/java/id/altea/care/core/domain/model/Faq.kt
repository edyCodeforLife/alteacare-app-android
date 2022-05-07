package id.altea.care.core.domain.model

import com.google.gson.annotations.SerializedName

data class Faq (
    val answer: String?,
    val faqId: String?,
    val question: String?
)