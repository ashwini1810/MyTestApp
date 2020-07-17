package com.gullybuy.componenet.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gullybuy.componenet.add_image_util.R
import com.gullybuy.componenet.util.Helper
import com.gullybuy.componenet.util.ImageObj
import com.gullybuy.componenet.util.ImageUtil
import com.yalantis.ucrop.UCrop


class AddImageDialog() : BottomSheetDialogFragment() {

    private val SAVE_EDITED_IMAGE: Int = 2
    private val SELECT_IMAGE = 1

    var TAG: String = "AddImageDialog"

    var imageSavedCallback: ImageSavedCallback? = null

    var activity: Activity? = Activity()

    fun setActivityContext(activity: Activity) {
        this.activity = activity
    }

    fun setCallBackListener(imageSavedCallback: ImageSavedCallback) {
        this.imageSavedCallback = imageSavedCallback
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        var btn_take_photo: TextView? = view.findViewById<TextView>(R.id.btn_take_photo)
        if (btn_take_photo != null) {
            btn_take_photo.setOnClickListener(View.OnClickListener {
                startCamera()
            })
        }

        var btn_select_photo: TextView? = view.findViewById<TextView>(R.id.btn_select_photo)
        if (btn_select_photo != null) {
            btn_select_photo.setOnClickListener(View.OnClickListener {
                startGallery()
            })
        }
    }

    private fun startCamera() {
        var intent_startCamera = Intent(context, CapturePhotoActivity::class.java)
        startActivityForResult(intent_startCamera, SAVE_EDITED_IMAGE);
    }

    fun startGallery() {
        var intent_startCamera = Intent(context, SelectPhotoActivity::class.java)
        startActivityForResult(intent_startCamera, SAVE_EDITED_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                SAVE_EDITED_IMAGE -> {
                    var fileUri: Uri = data?.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI)!!
                    var helper: Helper = Helper()
                    var new_img_length = this.context?.let { helper.getFileLength(it, fileUri.path) }
                    imageSavedCallback?.onCropFinish(ImageObj(fileUri.path, new_img_length!!))
                }
                SELECT_IMAGE -> {
                    val selectedImageUri = data!!.data
                    if (selectedImageUri != null) {
                        loadEditImageActivity(selectedImageUri, activity)
                    }
                }
            }
        }
        dismiss()
    }

    private fun loadEditImageActivity(selectedImageUri: Uri, activity1: Activity?) {
        val imageUtil = ImageUtil()
        if (activity1 != null) {
            imageUtil.loadEditImageActivity(selectedImageUri, activity1)
        }
    }


}