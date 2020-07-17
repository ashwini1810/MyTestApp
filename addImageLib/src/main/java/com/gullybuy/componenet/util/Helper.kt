package com.gullybuy.componenet.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.yalantis.ucrop.UCrop
import java.io.File

class Helper {

    public fun getRealPathFromURI(activity: Context?, contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = activity?.contentResolver?.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    public fun getFileLength(activity: Context, contentURI: String?): Long {
        var oldImgFile = File(getRealPathFromURI(activity, contentURI!!))
        var fileSize = oldImgFile.length()
        fileSize = fileSize / 1024
        return fileSize;
    }

    public fun deleteFile(fileName: String?) {
        val fileTobeDeleted = File(fileName)
        fileTobeDeleted.delete()
    }



}