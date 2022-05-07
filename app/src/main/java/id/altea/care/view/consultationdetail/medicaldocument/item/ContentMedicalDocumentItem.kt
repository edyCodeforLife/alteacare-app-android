package id.altea.care.view.consultationdetail.medicaldocument.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemMedicalDocumentContentBinding
import id.altea.care.view.consultationdetail.patientdata.item.UploadDocumentItem

class ContentMedicalDocumentItem(
    val dummyFile: DummyFile,
    var status : StatusUpload
) : AbstractBindingItem<ItemMedicalDocumentContentBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMedicalDocumentContentBinding {
        return ItemMedicalDocumentContentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMedicalDocumentContentBinding, payloads: List<Any>) {
        binding.run {
            itemMedicalDocTxtFileName.text = dummyFile.fileName
            itemMedicalDocTxtFileSize.text = dummyFile.fileSize
            itemMedicalDocTxtFileDate.text = dummyFile.fileDate

            when (status) {
                StatusUpload.LOADING -> {
                    binding.itemMedicalDocProgressBar.isVisible = true
                    binding.itemMedicalDocTxtShow.isVisible = false
                }
                StatusUpload.ERROR -> {
                    binding.itemMedicalDocProgressBar.isVisible = false
                    binding.itemMedicalDocTxtShow.isVisible = true
                }
               StatusUpload.SUCCESS -> {
                    binding.itemMedicalDocProgressBar.isVisible = false
                    binding.itemMedicalDocTxtShow.isVisible = true
                }
            }

        }
    }
}

data class DummyFile(
    val fileName: String,
    val fileSize: String,
    val filePath: String,
    val fileDate: String
)

enum class StatusUpload {
    SUCCESS, ERROR, LOADING
}
