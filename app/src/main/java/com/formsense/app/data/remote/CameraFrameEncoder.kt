package com.formsense.app.data.remote

import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

/** Converts CameraX YUV_420_888 frames to JPEG; caller retains ImageProxy ownership. */
object CameraFrameEncoder {
    fun toJpeg(image: ImageProxy, quality: Int = 65): ByteArray? {
        if (image.format != ImageFormat.YUV_420_888) return null
        val width=image.width; val height=image.height
        val ySize=width*height
        val nv21=ByteArray(ySize+ySize/2)
        val y=image.planes[0]
        copyPlane(y.buffer, y.rowStride, y.pixelStride, width, height, nv21, 0)
        val u=image.planes[1]; val v=image.planes[2]
        val ub=u.buffer.duplicate(); val vb=v.buffer.duplicate()
        var out=ySize
        for (row in 0 until height/2) for (col in 0 until width/2) {
            nv21[out++]=vb.get(row*v.rowStride+col*v.pixelStride)
            nv21[out++]=ub.get(row*u.rowStride+col*u.pixelStride)
        }
        val stream=ByteArrayOutputStream()
        val success=YuvImage(nv21, ImageFormat.NV21, width, height, null)
            .compressToJpeg(Rect(0,0,width,height), quality.coerceIn(30,95), stream)
        return if(success) stream.toByteArray() else null
    }

    private fun copyPlane(buffer: java.nio.ByteBuffer, rowStride: Int, pixelStride: Int,
        width: Int, height: Int, output: ByteArray, offset: Int) {
        val source=buffer.duplicate(); var index=offset
        for(row in 0 until height) for(col in 0 until width)
            output[index++]=source.get(row*rowStride+col*pixelStride)
    }
}
