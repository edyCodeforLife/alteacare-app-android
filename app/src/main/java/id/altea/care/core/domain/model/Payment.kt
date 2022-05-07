package id.altea.care.core.domain.model

data class Payment(
    val redirectUrl: String?,
    val token: String?,
    val total: Double?,
    val refId: String?,
    val bank: String?,
    val expiredAt: String?,
    val paymentUrl: String?,
    val type: String?,
    val vaNumber: String?,
)
