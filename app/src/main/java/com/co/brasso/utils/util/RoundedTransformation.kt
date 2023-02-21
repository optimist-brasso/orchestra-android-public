package com.co.brasso.utils.util

import android.graphics.*
import com.squareup.picasso.Transformation

class RoundedTransformation(
    private var radius: Float, // dp
    private var margin: Float
) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint()
        paint.setAntiAlias(true)
        paint.setShader(BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawRoundRect(
            RectF(
                margin.toFloat(),
                margin.toFloat(),
                (source.width - margin).toFloat(),
                (source.height - margin).toFloat()
            ), radius, radius, paint
        )
        if (source != output) {
            source.recycle()
        }
        return output
    }
    override fun key(): String? {
        return "rounded(radius=$radius, margin=$margin)"
    }
    fun RoundedTransformation(radius: Int, margin: Int) {
        this.radius = radius.toFloat()
        this.margin = margin.toFloat()
    }

}