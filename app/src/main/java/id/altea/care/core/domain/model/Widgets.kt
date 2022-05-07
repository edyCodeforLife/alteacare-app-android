package id.altea.care.core.domain.model

data class Widgets(
    val id : String,
    val title : String,
    val icon : String,
    val deepLinkType : String,
    val deeplinkUrl : String,
    val needLogin : Boolean
)
