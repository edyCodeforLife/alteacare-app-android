package id.altea.care.core.domain.model

data class FilesUpload(
    val id : Int?,
    val fileId : String?,
    val name : String?,
    val url : String?,
    val size : Double?,
    val formats : FormatImage?
)