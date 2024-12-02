package com.bangkit.submissionintermediet.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun createCustomTempFile(context: Context): File {
    return File.createTempFile(timeStamp, ".jpg", context.externalCacheDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    }
    return myFile
}

fun File.reduceFileImage(): File {
    val bitmap = BitmapFactory.decodeFile(this.path).getRotatedBitmap(this)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        streamLength = bmpStream.toByteArray().size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    return this
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(270F)
        ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_UNDEFINED -> this
        else -> this
    }
}

fun Bitmap.rotateImage(angle: Float): Bitmap? {
    val matrix = Matrix().apply { postRotate(angle) }
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}
