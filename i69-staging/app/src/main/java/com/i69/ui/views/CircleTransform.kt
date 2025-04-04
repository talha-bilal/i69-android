package com.i69.ui.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CircleTransform(private val borderWidth: Int, private val borderColor: Int) : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        // Create a new bitmap with the circular crop and border
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the border
        val paint = Paint()
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth.toFloat()
        canvas.drawCircle(width / 2f, height / 2f, width / 2f - borderWidth / 2f, paint)

        // Draw the circular crop
        val cropRect = Rect(borderWidth / 2, borderWidth / 2, width - borderWidth / 2, height - borderWidth / 2)
        val paintCircle = Paint()
        paintCircle.isAntiAlias = true
        val circlePath = Path()
        circlePath.addCircle(width / 2f, height / 2f, width / 2f - borderWidth / 2f, Path.Direction.CW)
        canvas.clipPath(circlePath)
        canvas.drawBitmap(toTransform, null, cropRect, null)

        return bitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("CircleTransform".toByteArray(Charsets.UTF_8))
        messageDigest.update(borderWidth.toString().toByteArray(Charsets.UTF_8))
        messageDigest.update(borderColor.toString().toByteArray(Charsets.UTF_8))
    }
}