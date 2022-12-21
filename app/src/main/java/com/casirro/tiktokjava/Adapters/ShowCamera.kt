package com.casirro.tiktokjava.Adapters

import android.annotation.SuppressLint
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.media.MediaRecorder
import android.media.CamcorderProfile
import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import java.io.IOException

@SuppressLint("ViewConstructor")
class ShowCamera(context: Context?, var camera: Camera?) : SurfaceView(context),
    SurfaceHolder.Callback {
    var mrec: MediaRecorder? = null
    override fun surfaceCreated(holder: SurfaceHolder) {
        val params = camera!!.parameters
        if (this.resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
            params["orientation"] = "portrait"
            camera!!.setDisplayOrientation(90)
            params.setRotation(90)
        } else {
            params["orientaion"] = "landscape"
            camera!!.setDisplayOrientation(0)
            params.setRotation(0)
        }
        camera!!.parameters = params
        try {
            camera!!.setPreviewDisplay(holder)
            camera!!.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera!!.stopPreview()
        camera!!.release()
    }

    fun startRecording() {
        mrec!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mrec!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mrec!!.setCamera(camera)
        val cpHigh = CamcorderProfile
            .get(CamcorderProfile.QUALITY_HIGH)
        mrec!!.setProfile(cpHigh)
        mrec!!.setOutputFile("/sdcard/videocapture_example.mp4")
        mrec!!.setOrientationHint(rotation.toInt())
        mrec!!.setMaxDuration(50000) // 50 seconds
        mrec!!.setMaxFileSize(5000000)

    }

    private fun stopRecording() {
        mrec!!.stop()
        mrec!!.release()
        camera!!.release()
    }

    private fun releaseMediaRecorder() {
        if (mrec != null) {
            mrec!!.reset() // clear mrec configuration
            mrec!!.release() // release the mrec object
            mrec = null
            camera!!.lock() // lock camera for later use
        }
    }

    private fun releaseCamera() {
        if (camera != null) {
            camera!!.release() // release the camera for other applications
            camera = null
        }
    }

    init {
        holder.addCallback(this)
    }
}