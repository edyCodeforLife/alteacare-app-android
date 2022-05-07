package id.altea.care.core.helper

import id.altea.care.core.domain.event.UploadProgressAdapter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

// see https://medium.com/@pranay1494/uploading-media-with-progress-using-retrofit-58cf8de62188

class ProgressEmittingRequestBody constructor(
    private val mediaType: String,
    private val file: File,
    private val adapterPosition: Int
) : RequestBody() {

    override fun contentType(): MediaType? = mediaType.toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(BUFFER_SIZE)
        var uploaded: Long = 0
        val fileSize = file.length()

        try {
            while (true) {
                val read = inputStream.read(buffer)
                if (read == -1) break
                uploaded += read
                sink.write(buffer, 0, read)
                val progress = (((uploaded / fileSize.toDouble())) * 100).toInt()
                RxBus.post(
                    UploadProgressAdapter.Loading(
                        adapterPosition,
                        if (progress == 100) 99 else progress
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            RxBus.post(UploadProgressAdapter.Error(adapterPosition))
        } finally {
            inputStream.close()
        }
    }

    override fun contentLength(): Long {
        return file.length()
    }

    companion object {
        const val BUFFER_SIZE = 2048
    }
}
