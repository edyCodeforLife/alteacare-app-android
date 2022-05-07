package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.AddDocumentData

@Keep
data class AddDocumentResponse(
    @SerializedName("date")
    val date: String?,
    @SerializedName("date_raw")
    val dateRaw: String?,
    @SerializedName("file_id")
    val fileId: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("size")
    val size: String?,
    @SerializedName("upload_by_user")
    val uploadByUser: Int?,
    @SerializedName("url")
    val url: String?
){
    fun toAddDocumentData() : AddDocumentData{
        return AddDocumentData(
            date,
            dateRaw,
            fileId,
            id,
            originalName,
            size,
            uploadByUser,
            url
        )
    }
}