package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.OperationalHourMA

@Keep
data class OperationalHourMAResponse(
    @SerializedName("operational_hour_start")
    val operationalHourStart: String? = "",
    @SerializedName("operational_hour_end")
    val operationalHourEnd: String? = "",
    @SerializedName("operational_work_day")
    val operationalWorkDay: List<String?>? = emptyList(),
    @SerializedName("first_delay_socket_connect")
    val firstDelaySocketConnect : Int? = 0,
    @SerializedName("delay_socket_connect")
    val delaySocketConnect : Int? = 0
) {
    companion object {
        fun OperationalHourMAResponse.mapToOperationalHourMA(): OperationalHourMA {
            return this.let { dataOperationalHourMA ->
                OperationalHourMA(
                    operationalHourStart = dataOperationalHourMA.operationalHourStart,
                    operationalHourEnd = dataOperationalHourMA.operationalHourEnd,
                    operationalHour = "${dataOperationalHourMA.operationalHourStart} - ${dataOperationalHourMA.operationalHourEnd}",
                    operationalWorkDay = dataOperationalHourMA.operationalWorkDay,
                    firstDelaySocketConnect = firstDelaySocketConnect,
                    delaySocketConnect = delaySocketConnect
                )
            }
        }
    }
}
