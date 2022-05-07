package id.altea.care.core.domain.model

data class PromotionList(
    val promotionId : Int,
    val status : Boolean,
    val title : String,
    val promotionType : String,
    val weight : Int,
    val image : String
)
