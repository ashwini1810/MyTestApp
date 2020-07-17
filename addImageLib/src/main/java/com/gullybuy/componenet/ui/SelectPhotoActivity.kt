package com.gullybuy.componenet.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gullybuy.componenet.util.ImageUtil
import com.gullybuy.componenet.util.Logger

class SelectPhotoActivity : AppCompatActivity() {
    private val SELECT_PICTURE = 1
    var TAG: String = "SelectPhotoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.writeLogToFile(TAG, "OnActivityResult");
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE) {
            // handle chosen image
            val selectedImageUri = data!!.data
           val imageUtil : ImageUtil = ImageUtil()
            if (selectedImageUri != null) {
                imageUtil.loadEditImageActivity(selectedImageUri, this)
            }
        }
        this.finish()
    }

    override fun onBackPressed() {
        this.finish()
    }


}