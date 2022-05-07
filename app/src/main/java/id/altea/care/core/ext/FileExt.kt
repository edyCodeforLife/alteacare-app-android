package id.altea.care.core.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


fun File.compressImage(
    preferredQuality: Int = 80,
    preferredResolution: Int = 2400,
    tempFolder: File?
): File {
    Timber.i("Trying to Compress Image in ${Thread.currentThread()}")
    try {
        val rotation = getRotation(absolutePath)
        val srcImage = BitmapFactory.decodeFile(path)
        val outputImage = ByteArrayOutputStream()

        // calculate scale of image
        val heightDiff =
            if (srcImage.height > preferredResolution) srcImage.height - preferredResolution else 0
        val newHeight =
            if (srcImage.height > preferredResolution) preferredResolution else srcImage.height
        val newWidth = srcImage.width - heightDiff


        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        // scale and compress image
        val scaledImage = Bitmap.createScaledBitmap(srcImage, newWidth, newHeight, true)
        val rotateBitmap = Bitmap.createBitmap(scaledImage, 0, 0, newWidth, newHeight, matrix, true)
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, preferredQuality, outputImage)

        // logging
        val srcImageSize = length() / 1024
        val newImageSize = outputImage.size() / 1024
        val sizeDiff = srcImageSize - newImageSize
        Timber.i(
            "Saved ${sizeDiff}kb around ${
                (sizeDiff * 100) / srcImageSize
            }% from original size of ${srcImageSize}kb to compressed ${newImageSize}kb"
        )

        tempFolder?.let {
            outputImage.toByteArray()
            val tempFileName = "${it.absolutePath}/altea-${absoluteFile.name}"
            val fileOutputStream = FileOutputStream(tempFileName)
            fileOutputStream.write(outputImage.toByteArray())
            return File(tempFileName)
        }
    } catch (e: Exception) {
        Timber.e(e)
    }
    return this
}

private fun getRotation(filename: String): Int {
    val orientation: Int = getExifTag(filename).toInt()
    Timber.i("Detected orientation: $orientation")
    var rotation = 0
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
        ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
        ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
    }
    Timber.i("Determined rotation: $rotation")
    return rotation
}

private fun getExifTag(filename: String): String {
    return try {
        val exif = ExifInterface(filename)
        return exif.getAttribute(ExifInterface.TAG_ORIENTATION)!!
    } catch (e: IOException) {
        Timber.e(e)
        "0"
    }
}