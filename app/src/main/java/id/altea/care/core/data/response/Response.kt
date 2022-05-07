package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName


open class Response<T>(
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("message")
    val message : String?,
    @SerializedName("data")
    val data : T)

open class MetaBaseResponse<T>(
    @SerializedName("meta")
    val metaResponse: MetaResponse?,
    status: Boolean?,
    message: String?,
    data: T
) : Response<T>(status, message, data)

data class DataResponse(
    @SerializedName("expire_at")
    val expireAt: String?,
    @SerializedName("identity")
    val identity: String?,
    @SerializedName("room_code")
    val roomCode: String?,
    @SerializedName("token")
    val token: String?)

data class DataResponseUpload(
    @SerializedName("id")
    val id : String?,
    @SerializedName("file_id")
    val file_id : String?,
    @SerializedName("name")
    val name : String?,
    @SerializedName("url")
    val url : String?,
    @SerializedName("size")
    val size : Double?,
    @SerializedName("formats")
    val formats : FormatImage
)

data class FormatImage(
    @SerializedName("thumbnail")
    val thumbnail : String?,
    @SerializedName("large")
    val large : String?,
    @SerializedName("medium")
    val medium : String?,
    @SerializedName("small")
    val small : String?
)

open class MetaBaseResponseGeneralSearch<T>(
    @SerializedName("meta")
    val metaResponse: MetaResponseGeneralSearch?,
    status: Boolean?,
    message: String?,
    data: T
) : Response<T>(status, message, data)