package com.gullybuy.componenet.util

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.gullybuy.componenet.add_image_util.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraUtil {

    private lateinit var photoFile: File
    var TAG: String = "CameraUtil";
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    val DEF_PHOTO_WIDTH = 600;
    val DEF_PHOTO_HEIGHT = 800;

    var photoSize: Size? = Size(DEF_PHOTO_WIDTH, DEF_PHOTO_HEIGHT);
    var camera: Camera? = null
    var imageCapture: ImageCapture? = null
    var activity: Activity? = null

    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    var lifecycleOwner: LifecycleOwner
    var viewFinder: PreviewView? = null

    constructor(activity: Activity, lifecycleOwner: LifecycleOwner, viewFinder: PreviewView) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner
        this.viewFinder = viewFinder
    }

    fun setPhotoSize(width: Int, height: Int) {
        this.photoSize = Size(width, height);
    }

    fun setLensFacing(lensFacing: Int) {
        this.lensFacing = lensFacing
        loadCamera(viewFinder)
    }

    fun setCameraFlashMode(flashMode: Int) {
        this.flashMode = flashMode
        loadCamera(viewFinder)
    }

    fun loadCamera(viewFinder: PreviewView?) {
        val cameraProviderFuture = activity?.let { ProcessCameraProvider.getInstance(it) }
        cameraProviderFuture?.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            var preview = Preview.Builder().build()

            imageCapture = photoSize?.let {
                ImageCapture.Builder()
                        .setTargetResolution(it)
                        .setFlashMode(flashMode)
                        .build()
            }

            // Select back camera
            val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                /*camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview)*/

                camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)

                preview?.setSurfaceProvider(viewFinder?.createSurfaceProvider(camera?.cameraInfo))
                //preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun getCameraObj(): Camera? {
        return this.camera
    }

    fun getPhotoFilePath(): Uri {
        return Uri.fromFile(photoFile);
    }

    fun capturePhoto(imageSavedCallback: ImageCapture.OnImageSavedCallback) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        var outputDirectory = getOutputDirectory()
        // Create timestamped output file to hold the image
        photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken

        playCaptureSound()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(activity), imageSavedCallback);

    }

    private fun playCaptureSound() {
        val volume = activity?.getSystemService(Context.AUDIO_SERVICE)
        if (volume != 0) {
            var _shootMP: MediaPlayer? = null
            if (_shootMP == null) _shootMP = MediaPlayer.create(activity, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"))
            if (_shootMP != null) _shootMP.start()
        }
    }

    fun getOutputDirectory(): File? {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, activity?.resources?.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity?.filesDir
    }


}