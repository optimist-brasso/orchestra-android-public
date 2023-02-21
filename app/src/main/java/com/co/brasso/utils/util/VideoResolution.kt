package com.co.brasso.utils.util

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import com.google.android.exoplayer2.util.MimeTypes
import okhttp3.internal.notifyAll

object VideoResolution {

    private fun selectCodec(mimeType: String): MediaCodecInfo? {
        val numCodecs: Int = MediaCodecList.getCodecCount()
        for (i in 0 until numCodecs) {
            val codecInfo: MediaCodecInfo = MediaCodecList.getCodecInfoAt(i)
            if (!codecInfo.isEncoder) {
                continue
            }
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].equals(mimeType, ignoreCase = true)) {
                    return codecInfo
                }
            }
        }
        return null
    }

    fun isVideoResolutionSupported(mimeType: String, widht: Int, height: Int): Boolean? {
        //possible mimeTypes
        //MimeTypes.VIDEO_H264
        // MimeTypes.VIDEO_H265
        //  MimeTypes.VIDEO_H263
        //for 4K video
        //widht   3840
        //height  2160
        val mediaCodecInfo = selectCodec(mimeType)
        mediaCodecInfo?.name
        val codecCapabilities = mediaCodecInfo?.getCapabilitiesForType(MimeTypes.VIDEO_H264)
        val videoCapabilities = codecCapabilities?.videoCapabilities
        return videoCapabilities?.isSizeSupported(widht, height)

    }

    fun is4kSupported():Boolean?{
        val mediaCodecInfo = selectCodec(MimeTypes.VIDEO_H264)
        mediaCodecInfo?.name
        val codecCapabilities = mediaCodecInfo?.getCapabilitiesForType(MimeTypes.VIDEO_H264)
        val videoCapabilities = codecCapabilities?.videoCapabilities
        return videoCapabilities?.isSizeSupported(3840, 2160)
    }
}