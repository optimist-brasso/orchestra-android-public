package com.co.brasso.utils.util

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.co.brasso.BuildConfig
import com.co.brasso.utils.constant.Constants
import com.facebook.FacebookSdk.getApplicationContext
import io.reactivex.Single
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws
import kotlin.math.roundToInt


object ImageCompressUtils {

    fun compressImage(context: Context?, imageUri: Uri?, type: String) =
        Single.create<File> {
            var scaledBitmap: Bitmap? = null
            var bmp: Bitmap? = null
            var filePath: String? = null

            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            if (imageUri != null)
                filePath = getFilePathFromUri(context!!, imageUri, false)
            bmp = context?.getBitmapFromUri(imageUri, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

            if (actualHeight != 0 && actualWidth != 0) {
                val maxHeight = 1280.0f
                val maxWidth = 720.0f
                var imgRatio = (actualWidth / actualHeight).toFloat()
                val maxRatio = maxWidth / maxHeight
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    when {
                        imgRatio < maxRatio -> {
                            imgRatio = maxHeight / actualHeight
                            actualWidth = (imgRatio * actualWidth).toInt()
                            actualHeight = maxHeight.toInt()
                        }
                        imgRatio > maxRatio -> {
                            imgRatio = maxWidth / actualWidth
                            actualHeight = (imgRatio * actualHeight).toInt()
                            actualWidth = maxWidth.toInt()
                        }
                        else -> {
                            actualHeight = maxHeight.toInt()
                            actualWidth = maxWidth.toInt()
                        }
                    }
                }

                //setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

                //inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false

                //this options allow android to claim the bitmap memory if it runs low on memory

                options.inPurgeable = true

                options.inInputShareable = true
                options.inTempStorage = ByteArray(16 * 1024)

                try {
                    //load the bitmap from its path
                    bmp = context?.getBitmapFromUri(imageUri, options)

                } catch (exception: OutOfMemoryError) {
                    exception.printStackTrace()
                }

                if (actualWidth > 0f && actualHeight > 0f) {
                    try {
                        scaledBitmap =
                            Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
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
                    if (bmp != null)
                        canvas.drawBitmap(
                            bmp, middleX - bmp.width / 2, middleY - bmp.height / 2,
                            Paint(Paint.FILTER_BITMAP_FLAG)
                        )

                    //check the rotation of the image and display it properly
                    val exif: android.media.ExifInterface
                    try {
                        exif = android.media.ExifInterface(filePath.toString())

                        val orientation =
                            exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 0)
                        Log.d("EXIF", "Exif: $orientation")
                        val matrix = Matrix()
                        when (orientation) {
                            6 -> {
                                matrix.postRotate(90f)
                                Log.d("EXIF", "Exif: $orientation")
                            }
                            3 -> {
                                matrix.postRotate(180f)
                                Log.d("EXIF", "Exif: $orientation")
                            }
                            8 -> {
                                matrix.postRotate(270f)
                                Log.d("EXIF", "Exif: $orientation")
                            }
                        }
                        scaledBitmap = Bitmap.createBitmap(
                            scaledBitmap, 0, 0, scaledBitmap.width,
                            scaledBitmap.height, matrix, true
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val out: FileOutputStream
                    val file = getFile()
                    try {
                        out = FileOutputStream(file)
                        //write the compressed bitmap at the destination specified by filename.
                        scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    it.onSuccess(file)
                } else {
                    it.onSuccess(File(""))
                }
            }
            it.onSuccess(File(""))

        }
}

private fun getFile(): File {
    val cw = ContextWrapper(getApplicationContext())
    val directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val folder = File(
        directory
            .toString() + File.separator + Constants.fileDirectory
    )
    if (!folder.exists()) {
        folder.mkdirs()
    }
    return File(
        directory
            .toString() + File.separator + Constants.fileDirectory +
                File.separator + System.currentTimeMillis() + ".jpg"
    )
}

fun getRealPathFromURI(context: Context?, contentUri: Uri?): String? {
    if (contentUri == null)
        return null
    val cursor = context?.contentResolver?.query(contentUri, null, null, null, null)
    return if (cursor == null) {
        contentUri.path
    } else {
        cursor.moveToFirst()
        val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val s = cursor.getString(index)
        cursor.close()
        s
    }
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}

private fun getGooglePhotosBitmap(context: Context?, imageUri: Uri?): Bitmap {
    val content = context?.contentResolver?.openInputStream(imageUri ?: Uri.EMPTY)
    return BitmapFactory.decodeStream(content)
}

fun getFileUri(c: Context?, file: File?): Uri? {
    return c?.let {
        file?.let { it1 ->
            FileProvider.getUriForFile(
                it, BuildConfig.APPLICATION_ID + ".fileprovider", it1
            )
        }
    }
}

fun createFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val cw = ContextWrapper(getApplicationContext())
    val directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, ".jpg", directory).absoluteFile
}

fun bitMapToFile(bitMap: Bitmap?): File? {
    val out: FileOutputStream
    val file = getFile()
    try {
        out = FileOutputStream(file)
        //write the compressed bitmap at the destination specified by filename.
        bitMap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    return file
}

@Throws(IOException::class)
fun Context.getBitmapFromUri(uri: Uri?, options: BitmapFactory.Options? = null): Bitmap? {
    if (uri == null)
        return null
    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor = parcelFileDescriptor?.fileDescriptor
    val image: Bitmap? = if (options != null)
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
    else
        BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor?.close()
    return image
}
