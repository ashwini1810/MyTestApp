package com.gullybuy.componenet.util

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.gullybuy.componenet.add_image_util.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImageUtil {
    fun loadEditImageActivity(selectedImageUri: Uri, activity: Activity) {

        val file = File(Environment.getExternalStorageDirectory().path, "GullyBuy/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        var fileName = file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"

        var uCrop = UCrop.of(selectedImageUri, Uri.fromFile(File(fileName)))
        var options: UCrop.Options = UCrop.Options()
        var resource = activity.resources
        options.setStatusBarColor(resource.getColor(R.color.colorPrimary))
        options.setToolbarColor(resource.getColor(R.color.colorPrimaryDark))
        options.setToolbarWidgetColor(resource.getColor(R.color.ucrop_color_white))
        options.setToolbarTitle(resource.getString(R.string.title_edit_image))
        uCrop.withOptions(options)
        var mCropIntent = uCrop.getIntent(activity)
        mCropIntent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT;
        activity.startActivity(mCropIntent)

        /* var mCropIntent =Intent(activity,MyUCropActivity::class.java);
         val mCropOptionsBundle = Bundle()
         mCropOptionsBundle.putParcelable(UCrop.EXTRA_INPUT_URI, selectedImageUri)
         mCropOptionsBundle.putParcelable(UCrop.EXTRA_OUTPUT_URI,  Uri.fromFile(File(fileName)))
         mCropOptionsBundle.putParcelable(UCrop.EXTRA_STATUS_BAR_COLOR,  Uri.fromFile(File(fileName)))
         mCropIntent.putExtras(mCropOptionsBundle)
         mCropIntent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT;*/

    }

    public fun compress(activity: Activity?, imageUri: Uri?): String {
        var helper: Helper = Helper()
        val filePath = helper.getRealPathFromURI(activity, "" + imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 800.0f
        val maxWidth = 600.0f
        var imgRatio = actualWidth / actualHeight.toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.width, scaledBitmap.height, matrix,
                    true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = filename

        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = width * height.toFloat()
        val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    val filename: String
        get() {
            val file = File(Environment.getExternalStorageDirectory().path, "GullyBuy/Images")
            if (!file.exists()) {
                file.mkdirs()
            }
            return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
        }
}