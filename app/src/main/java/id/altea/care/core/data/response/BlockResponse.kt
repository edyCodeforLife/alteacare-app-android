package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Block

@Keep
data class BlockResponse(
    @SerializedName("block_id")
    val blockId: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?
){
    fun toBlock() : Block{
        return Block(blockId,text,title,type)
    }
}