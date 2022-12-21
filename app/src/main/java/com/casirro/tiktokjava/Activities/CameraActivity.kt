package com.casirro.tiktokjava.Activities

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.Toast
import com.casirro.tiktokjava.databinding.ActivityCameraBinding
import java.io.IOException

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var camera: Camera
    private val CAMERA_PERMISSION_REQUEST_CODE = 1
    private lateinit var surfaceView: SurfaceView
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        binding.imageView4.bringToFront()
        binding.imageView19.bringToFront()
        binding.imageView21.bringToFront()
        binding.imageView24.bringToFront()
        binding.imageView25.bringToFront()
        binding.imageView26.bringToFront()
        binding.imageView27.bringToFront()
        binding.imageView28.bringToFront()
        binding.imageView29.bringToFront()
        binding.imageView30.bringToFront()
        binding.imageView31.bringToFront()
        binding.view14.bringToFront()
        binding.textView27.bringToFront()
        binding.textView28.bringToFront()
        binding.textView35.bringToFront()
        binding.imageView24.setOnClickListener { finish() }
        binding.imageView4
        binding.imageView19.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 75)
        }
        surfaceView = binding.frameLayout

        binding.imageView4.setOnClickListener {
            isRecording = if (isRecording) {
                stopVideoRecording()
                false
            } else {
                requestCameraPermission()
                initializeMediaRecorder()
                prepareMediaRecorder()
                startVideoRecording()
                true
            }
        }
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        camera = Camera.open()
        val parameters = camera.parameters
        parameters.setPreviewSize(640, 480)
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
        camera.parameters = parameters
        try {
            camera.setPreviewDisplay(surfaceView.holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        camera.startPreview()
    }

    private fun initializeMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setCamera(camera)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        setOutputFormatAndFileName()
        setVideoSizeAndFrameRate()
        setVideoAndAudioEncoder()
    }
    private fun setOutputFormatAndFileName() {
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setOutputFile(getOutputFilePath())
    }

    private fun getOutputFilePath(): String {
        val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return mediaStorageDir!!.absolutePath + "/" + System.currentTimeMillis() + ".mp4"
    }

    private fun setVideoSizeAndFrameRate() {
        mediaRecorder.setVideoSize(640, 480)
        mediaRecorder.setVideoFrameRate(30)
    }

    private fun setVideoAndAudioEncoder() {
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    }

    private fun prepareMediaRecorder() {
        try {
            mediaRecorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startVideoRecording() {
        mediaRecorder.start()
    }

    private fun stopVideoRecording() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        mediaRecorder.release()
        mediaRecorder = MediaRecorder()
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 75) {
            if (data != null) {
                val selectedVideo = data.data
                val intent = Intent(this@CameraActivity, videoProceedActivity::class.java)
                intent.putExtra("VideoUri", selectedVideo.toString())
                startActivity(intent)
            }
        }
    }


}