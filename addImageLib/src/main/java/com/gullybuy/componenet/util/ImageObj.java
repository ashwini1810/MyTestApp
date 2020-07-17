package com.gullybuy.componenet.util;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ImageObj implements Serializable {
    String imagePath;
    Long imageSize;

    public ImageObj(String imagePath, long imageSize) {
        super();
        this.imagePath = imagePath;
        this.imageSize = imageSize;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

    @NonNull
    @Override
    public String toString() {
        String imgObjStr = "Image path : " + imagePath + "\nImage Size : " + imageSize + "KB";
        return imgObjStr;
    }
}
