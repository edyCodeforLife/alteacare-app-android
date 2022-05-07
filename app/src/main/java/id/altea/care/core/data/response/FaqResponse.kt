package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Faq

@Keep
data class FaqResponse(
    @SerializedName("answer")
    val answer: String?,
    @SerializedName("faq_id")
    val faqId: String?,
    @SerializedName("question")
    val question: String?
){
    fun toFaq() : Faq{
        return Faq(
            answer,
            faqId,
            question
        )
    }
}