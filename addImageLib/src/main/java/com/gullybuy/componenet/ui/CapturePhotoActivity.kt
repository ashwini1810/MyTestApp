package com.gullybuy.componenet.ui


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import com.gullybuy.componenet.add_image_util.R
import com.gullybuy.componenet.util.CameraUtil
import com.gullybuy.componenet.util.Helper
import com.gullybuy.componenet.util.ImageUtil

class CapturePhotoActivity : AppCompatActivity(), ImageCapture.OnImageSavedCallback, ScaleGestureDetector.OnScaleGestureListener {
    companion object {
        var activity: Activity? =null}

    private lateinit var scaleDetector: ScaleGestureDetector
    var helper: Helper? = Helper()
    //  var zoomSlider: SeekBar? = null
    var cameraUtil: CameraUtil? = null

    private val TAG:String= "CapturePhotoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture);
        activity = this
        scaleDetector = ScaleGestureDetector(this, this)

        var viewFinder: PreviewView = findViewById(R.id.viewFinder)
        cameraUtil = CameraUtil(this, this, viewFinder)
        cameraUtil?.loadCamera(viewFinder)

        var camera_capture_button: ImageButton = findViewById(R.id.camera_capture_button)
        camera_capture_button.setOnClickListener {
            cameraUtil?.capturePhoto(this)
        }
        //setUpZoomSlider();
        var camera_flip: ImageButton = findViewById(R.id.camera_flip)
        var lenceFacing: Int = CameraSelector.LENS_FACING_BACK
        camera_flip.setOnClickListener {
            when (lenceFacing) {
                CameraSelector.LENS_FACING_BACK -> {
                    lenceFacing = CameraSelector.LENS_FACING_FRONT
                }
                CameraSelector.LENS_FACING_FRONT -> {
                    lenceFacing = CameraSelector.LENS_FACING_BACK
                }
            }
            //Restart Camera
            cameraUtil?.setLensFacing(lenceFacing)
        }

        var flashMode = ImageCapture.FLASH_MODE_OFF
        var camera_flash_mode: ImageButton = findViewById(R.id.camera_flash_mode)
        camera_flash_mode.setOnClickListener {
            when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode = ImageCapture.FLASH_MODE_ON
                    camera_flash_mode.setImageResource(R.drawable.ic_flash_on)
                }
                ImageCapture.FLASH_MODE_ON -> {
                    flashMode = ImageCapture.FLASH_MODE_AUTO
                    camera_flash_mode.setImageResource(R.drawable.ic_flash_auto)
                }
                ImageCapture.FLASH_MODE_AUTO -> {
                    flashMode = ImageCapture.FLASH_MODE_OFF
                    camera_flash_mode.setImageResource(R.drawable.ic_flash_off)
                }
            }
            //Restart Camera
            cameraUtil?.setCameraFlashMode(flashMode)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events
        scaleDetector.onTouchEvent(event)
        return true
    }



    override fun onError(exc: ImageCaptureException) {
        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
        Toast.makeText(this, "Photo capture failed", Toast.LENGTH_SHORT).show()
    }

    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val capturedImageUri = cameraUtil?.getPhotoFilePath()
        Log.d(TAG, "Photo captured succesfully" + capturedImageUri)
        //Compress caprured Image
      //  var compressImage: CompressImage = CompressImage()
     //   var compressedImagePath: String = compressImage.compress(this, capturedImageUri)

        /* var intent = Intent(this, PreviewActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT;
         intent.setData(capturedImageUri)
         intent.putExtra("ImageURI", "" + compressedImagePath)
         intent.putExtra("old_ImageURI", "" + capturedImageUri)
         startActivity(intent)*/

        if (capturedImageUri != null) {
            val imageUtil : ImageUtil = ImageUtil()
            if (capturedImageUri != null) {
                imageUtil.loadEditImageActivity(capturedImageUri, this)
            }

        };
        //Delete Original image

        helper?.deleteFile(capturedImageUri.toString())
        finish()
    }

    override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
        TODO("Not yet implemented")
    }

    override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector?) {
        TODO("Not yet implemented")
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (cameraUtil?.getCameraObj() != null) {
            val currentZoomRatio: Float = cameraUtil?.getCameraObj()!!.cameraInfo.zoomState.value?.zoomRatio?: 0F
            val delta = detector.scaleFactor
            cameraUtil?.getCameraObj()!!.cameraControl.setZoomRatio(currentZoomRatio * delta)
            //   Toast.makeText(this@CaptureImage,"currentZoomRatio "+currentZoomRatio,Toast.LENGTH_SHORT).show()
            //  zoomSlider?.setProgress((currentZoomRatio * 10).toInt())
        }
        return true
    }



    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d(TAG,"onActivityResult"+requestCode)

        if (resultCode == Activity.RESULT_OK && requestCode == 4) {
            // handle chosen image

            var resultIntent = Intent();
            resultIntent.putExtra("FILE_PATH", "selectedImagePath");
            setResult(Activity.RESULT_OK, resultIntent);
            this.finish()

        }
    }*/
}