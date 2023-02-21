package com.co.brasso.utils.util

import android.content.Context
import android.view.WindowManager
import java.lang.reflect.Field

object DensityUtils {
    fun dipTopxFloat(context: Context?, dpValue: Float): Float {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5f)
    }

    fun dipTopx(context: Context?, dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5f).toInt()
    }

    fun pxTodip(context: Context?, pxValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (pxValue / scale + 0.5f).toInt()
    }


    fun getScreenWidth(context: Context?): Int {
        val display = (context
            ?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        // Point p = new Point();
        // display.getSize(p);//need api13
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context?): Int {
        val display = (context
            ?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        // Point p = new Point();
        // display.getSize(p);//need api13
        return context.resources.displayMetrics.heightPixels
    }

    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var sbar = 38
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field.get(obj).toString())
            sbar = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return sbar
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    fun calculateLength(c: CharSequence): Long {
        var len = 0.0
        for (i in 0 until c.length) {
            val tmp = c[i].toInt()
            if (tmp > 0 && tmp < 127) {
                len += 0.5
            } else {
                len++
            }
        }
        return Math.round(len)
    }

    fun getImageHeight(context: Context?):Float{
        val screenWidth= getScreenWidth(context)
        return  ((1080*screenWidth).toFloat())/1920
    }

    fun getFeaturedImageHeight(context: Context?):Float{
        val screenWidth= getActualScreenWidth(context)
        return  ((1080*screenWidth).toFloat())/1920
    }

    fun getActualScreenWidth(context: Context?):Int{
        return (getScreenWidth(context)-(dipTopx(context,32f)))
    }

//    fun getListingImageHeight(context: Context?):Float{
//        val imageWidth= getActualScreenWidth(context).toFloat()/3
//        return  ((1080*imageWidth))/1920
//    }

    fun getListingImageHeight(context: Context?):Float{
        val imageWidth= getActualScreenWidth(context).toFloat()/3
        return  ((720*imageWidth))/1280
    }

    fun getListingEventImageHeight(context: Context?):Float{
        val imageWidth= getActualScreenWidth(context).toFloat()/3
        return  ((720*imageWidth))/1280
    }

    fun getMembershipWidth(context: Context?): Float {
        return getActualScreenWidth(context).toFloat()/1.8f
    }
}