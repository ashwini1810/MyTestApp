package com.gullybuy.componenet.ui

import com.gullybuy.componenet.util.ImageObj

interface ImageSavedCallback {
    fun onCropFinish(imageObj: ImageObj?)
}