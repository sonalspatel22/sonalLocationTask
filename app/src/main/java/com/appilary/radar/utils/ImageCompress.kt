package com.appilary.radar.utils

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.FileOutputStream
import java.io.IOException

private val maxHeight = 1280.0f
private val maxWidth = 1280.0f

suspend fun Context.compressImage(imagePath: String? = null, bitmap: Bitmap? = null): String? {
    if (imagePath.isNullOrEmpty() && bitmap == null)
        return null
    val filepath = AppUtils.getImagePath(this, "${System.currentTimeMillis()}COMPRESS_PRE")
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
//        val imagePath = AppUtils.getImagePath(context, imageName)
    var bmp: Bitmap? =
        if (imagePath.isNullOrEmpty()) bitmap else BitmapFactory.decodeFile(imagePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth
    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight
    if (actualHeight == 0 || actualWidth == 0) {
        actualHeight = maxHeight.toInt()
        actualWidth = maxWidth.toInt()
    } else if (actualHeight > maxHeight || actualWidth > maxWidth) {
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
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
    options.inJustDecodeBounds = false
    options.inDither = false
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
        bmp =
            if (imagePath.isNullOrEmpty()) bitmap else BitmapFactory.decodeFile(imagePath, options)


        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap ?: bmp ?: return filepath)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp?: return filepath,
            middleX - bmp!!.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        if (!imagePath.isNullOrEmpty()) {
            val exif: ExifInterface
            try {
                exif = ExifInterface(imagePath)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                when (orientation) {
                    6 -> {
                        matrix.postRotate(90f)
                    }
                    3 -> {
                        matrix.postRotate(180f)
                    }
                    8 -> {
                        matrix.postRotate(270f)
                    }
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    0,
                    scaledBitmap!!.width,
                    scaledBitmap.height,
                    matrix,
                    true
                )
            } catch (e: IOException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                e.printStackTrace()
            }
        }

        var out: FileOutputStream? = null

        out = FileOutputStream(filepath)
//write the compressed bitmap at the destination specified by filename.
        (scaledBitmap ?: bmp).compress(Bitmap.CompressFormat.JPEG, 80, out)

        bmp.recycle()
    } catch (e: Exception) {
        e.printStackTrace()
        FirebaseCrashlytics.getInstance().recordException(e)
    }
//        File(imagePath).delete() // Delete the original file.
    return filepath
}

suspend fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}