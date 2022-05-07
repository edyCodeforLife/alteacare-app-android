package id.altea.care.view.consultationdetail.patientdata.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemPatientUploadFileBinding

class UploadDocumentItem(
    var id: Int? = 0,
    val mimeType: String? = "image",
    var name: String? = null,
    var filePath: String? = null,
    var urlDownload: String? = null,
    var size: String? = "",
    var status: StatusUpload = StatusUpload.LOADING,
    var progress: Int = 0
) : AbstractBindingItem<ItemPatientUploadFileBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPatientUploadFileBinding {
        return ItemPatientUploadFileBinding.inflate(inflater)
    }

    fun setLoadingState(progressUpload: Int) {
        status = StatusUpload.LOADING
        progress = progressUpload
    }

    fun setSuccessState() {
        status = StatusUpload.SUCCESS
    }

    fun setErrorState() {
        status = StatusUpload.ERROR
    }

    override fun bindView(binding: ItemPatientUploadFileBinding, payloads: List<Any>) {
        binding.run {
            patientFileTxtFileName.text = name
            patientFileTxtFileSize.text = size

            when (status) {
                StatusUpload.LOADING -> {
                    binding.layoutProgress.isVisible = true
                    binding.itemPatientErrorStatus.isVisible = false
                    binding.patientFileTxtShow.visibility = View.INVISIBLE
                    binding.patientFileTxtDelete.visibility = View.INVISIBLE
                    binding.itemPatientProgressText.text = "$progress %"
                    binding.itemPatientProgressBar.progress = progress
                }
                StatusUpload.ERROR -> {
                    binding.layoutProgress.isVisible = false
                    binding.itemPatientErrorStatus.isVisible = true
                    binding.patientFileTxtShow.visibility = View.INVISIBLE
                    binding.patientFileTxtDelete.visibility = View.INVISIBLE
                }
                StatusUpload.SUCCESS -> {
                    binding.layoutProgress.isVisible = false
                    binding.itemPatientErrorStatus.isVisible = false
                    binding.patientFileTxtShow.visibility = View.VISIBLE
                    binding.patientFileTxtDelete.visibility = View.VISIBLE
                }
            }
        }
    }

    enum class StatusUpload {
        SUCCESS, ERROR, LOADING
    }
}