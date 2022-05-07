package id.altea.care.core.domain.model

data class PromotionListGroup(
    val promotionId : String,
    val promotionTitle : String,
    val promotionList : List<ItemPromotionListGroup?>? = emptyList()
)

data class ItemPromotionListGroup(
    val id : Int,
    val weight: Int,
    val image: String,
    val promotionType : String
)
