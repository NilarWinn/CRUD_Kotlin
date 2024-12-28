package com.example.bulletinboard.ui.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileUtil {

    companion object {

        fun getFile(context: Context, uri: Uri?): File? {
            if (uri == null) return null
            val file = File.createTempFile("tmp", ".${getExtension(context, uri)}")
            context.contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output -> input!!.copyTo(output) }
                file.deleteOnExit()
            }
            return file
        }

        fun getExtension(applicationContext: Context, uri: Uri): String? {
            val fileName = getFileName(applicationContext, uri)
            if (fileName != null) {
                return fileName.split('.')[1]
            }
            return null
        }

        private fun getFileName(applicationContext: Context, uri: Uri): String? {

            // Only for Document Uri
            if (uri.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = applicationContext.contentResolver.query(
                        uri, null, null, null, null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        return cursor.getString(
                            cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                        )
                    }
                } finally {
                    cursor?.close()
                }
            }
            return null
        }

        fun saveFile(filePlain: String): File? {

            val fileName = SimpleDateFormat("yyyyMMddHHmmssSSSSSS", Locale.getDefault())
                .format(Calendar.getInstance().time)
            val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "bulletin-$fileName.csv"
            )

            var inputStream: ByteArrayInputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = filePlain.byteInputStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val index = inputStream.read(fileReader)
                    if (index == -1) break
                    outputStream.write(fileReader, 0, index)
                }
                outputStream.flush()
            } catch (e: IOException) {
                return null
            }
            finally {
                inputStream?.close()
                outputStream?.close()
            }
            return file
        }
    }
}