package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.midtrans.sdk.corekit.models.TransactionDetails
import id.altea.care.core.domain.model.DetailTransaction

@Keep
data class DetailTransactionResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("provider")
    val provider: String?
){
    fun toDetailTransaction() : DetailTransaction {
        return DetailTransaction(
            code,
            description,
            icon,
            name,
            provider
        )
    }

}