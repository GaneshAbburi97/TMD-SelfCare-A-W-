package com.example.tmdapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageUtil {
    
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "profile_img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createTempImageUri(context: Context): Uri {
        val tempFile = File(context.cacheDir, "temp_profile_pic.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }
}
