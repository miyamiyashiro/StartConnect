package com.example.startconnect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ProfilePhotoHelper {

    private const val PREFS_NAME = "startconnect_prefs"
    private const val KEY_PHOTO_PREFIX = "profile_photo_"

    fun savePhoto(context: Context, usuarioId: Int, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Redimensionar pra nao ficar pesado
            val scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
            val baos = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString("$KEY_PHOTO_PREFIX$usuarioId", base64).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPhotoBitmap(context: Context, usuarioId: Int): Bitmap? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val base64 = prefs.getString("$KEY_PHOTO_PREFIX$usuarioId", null) ?: return null
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
