package id.altea.care.core.domain.model


data class OperationalHourMA(
    val operationalHourStart: String? = "",
    val operationalHourEnd: String? = "",
    val operationalHour: String? = "",
    val operationalWorkDay: List<String?>? = emptyList(),
    val firstDelaySocketConnect : Int? = 0,
    val delaySocketConnect : Int? = 0
)
