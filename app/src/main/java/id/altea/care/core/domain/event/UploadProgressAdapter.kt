package id.altea.care.core.domain.event

sealed class UploadProgressAdapter {
    data class Complete(val adapterPosition: Int) : UploadProgressAdapter()
    data class Loading(val adapterPosition: Int, val progress: Int) : UploadProgressAdapter()
    data class Error(val adapterPosition: Int) : UploadProgressAdapter()
}