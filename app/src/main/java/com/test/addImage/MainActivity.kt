package com.test.addImage

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testcamerax.R
import com.gullybuy.componenet.ui.AddImageDialog
import com.gullybuy.componenet.ui.ImageSavedCallback
import com.gullybuy.componenet.util.ImageObj
import com.gullybuy.componenet.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() , ImageSavedCallback {

    var dialog: AddImageDialog? = null
    var TAG : String ="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        dialog = AddImageDialog()
        dialog?.setActivityContext(this)
        dialog?.setCallBackListener(this)
        btn_upload_image.setOnClickListener {
            if (allPermissionsGranted()) {
                dialog!!.show(supportFragmentManager, "tag")
            }
        }


    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCropFinish(imageObj: ImageObj?) {
        var msg: String = imageObj.toString()
        Logger.log(TAG, msg)
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        var imageView : ImageView = findViewById(R.id.selected_img_view)
        if(imageObj!=null && imageObj.imagePath!=null)
        {
            imageView.setImageURI(Uri.fromFile(File(imageObj.imagePath)))
        }
    }


}