package id.altea.care.core.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import java.io.File


fun Activity.createDefaultImagePath(): File {
    val directoryStorage = getExternalFilesDir("Temp")
    if (directoryStorage != null && !directoryStorage.exists()) {
        if (directoryStorage.mkdirs()) {
            Timber.i("Temp folder createdd");
        }
    }
    return File.createTempFile("IMG-${System.currentTimeMillis()}", ".jpg", directoryStorage)
}
fun Activity.copyToClipboard(text : String){
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("voucherCode",text)
    clipboard.setPrimaryClip(clip)
}

fun Activity.getTempFolder(): File {
    return getExternalFilesDir("Temp")?.apply {
        if (!exists()) this.mkdir()
    } ?: this.filesDir
}



fun Activity.getRealPathURI(uri: Uri) : File?{
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    var fileReturn: File?
    this.contentResolver.query(uri, filePathColumn, null, null, null)?.let {
        it.moveToFirst()

        val columnIndex: Int = it.getColumnIndex(filePathColumn[0])
        fileReturn = File(it.getString(columnIndex))
        it.close()
        return fileReturn
    }
    return null
}

fun AppCompatActivity.openDocument( uri : Uri) {
    val intent =  Intent(Intent.ACTION_VIEW,uri)
    if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
        // Word document
        intent.setDataAndType(uri, "application/msword")
    } else if (uri.toString().contains(".pdf")) {
        // PDF file
        intent.setDataAndType(uri, "application/pdf")
    } else {
        // Other files
        intent.setDataAndType(uri, "*/*")
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    intent.putExtra("return-data", true)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    this.startActivity(intent)
}

