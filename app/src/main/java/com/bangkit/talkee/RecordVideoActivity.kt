package com.bangkit.talkee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bangkit.talkee.databinding.ActivityRecordVideoBinding
import java.io.File

class RecordVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordVideoBinding

    private lateinit var cameraDevice: CameraDevice
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var videoFile: File
    private lateinit var handler: Handler
    private lateinit var previewSize: Size

    private var accurateSeconds: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val screenWidth = binding.root.width
                setCameraSize(screenWidth)
            }
        })

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return
        }

        val handlerThread = HandlerThread("CameraThread").apply { start() }
        handler = Handler(handlerThread.looper)

        setupCamera()

        window.statusBarColor = getColor(R.color.black)

        Handler(Looper.getMainLooper()).postDelayed({
            startCountdown()
        }, 1000)
    }

    private fun setCameraSize(screenWidth: Int) {
        val boxHeight = screenWidth * 4 / 3

        val layoutParams = binding.textureView.layoutParams
        layoutParams.width = screenWidth
        layoutParams.height = boxHeight
        binding.textureView.layoutParams = layoutParams
    }

    private fun startCountdown() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.text1.text = accurateSeconds.toString()
                accurateSeconds--
            }

            override fun onFinish() {
                binding.text1.visibility = View.INVISIBLE
                binding.text2.visibility = View.VISIBLE
                startRecording()
            }
        }.start()
    }

    private fun startRecording() {
        videoFile = File(filesDir, "video.mp4")
        mediaRecorder = MediaRecorder().apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(videoFile.absolutePath)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setVideoSize(previewSize.width, previewSize.height)
            setVideoFrameRate(30)

            val rotation = windowManager.defaultDisplay.rotation
            val orientation = ORIENTATIONS.get(rotation)
            setOrientationHint(orientation)
            prepare()
        }

        val surface = Surface(binding.textureView.surfaceTexture)
        val recorderSurface = mediaRecorder.surface

        val captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(surface)
            addTarget(recorderSurface)
        }

        cameraDevice.createCaptureSession(listOf(surface, recorderSurface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                session.setRepeatingRequest(captureRequestBuilder.build(), null, handler)
                mediaRecorder.start()

                Handler(Looper.getMainLooper()).postDelayed({
                    stopRecording()
                }, RECORDING_DURATION)
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {}
        }, handler)
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.release()
        cameraDevice.close()

        Log.d("VIDEO LENGTH", getVideoDuration(videoFile).toString())

        val resultIntent = Intent().apply {
            data = Uri.fromFile(videoFile)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun getVideoDuration(file: File): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return durationString?.toLong() ?: 0L
    }

    private fun setupCamera() {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        var cameraId: String? = null

        for (id in manager.cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(id)
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraId = id
                break
            }
        }

        if (cameraId == null) {
            Log.e("CAMERA", "No front camera found")
            return
        }

        val characteristics = manager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        previewSize = map?.getOutputSizes(SurfaceTexture::class.java)?.get(0)!!

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                return
            }
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    val texture = binding.textureView.surfaceTexture
                    texture?.setDefaultBufferSize(previewSize.width, previewSize.height)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    cameraDevice.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    cameraDevice.close()
                }
            }, handler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val RECORDING_DURATION = 5000L
        private val ORIENTATIONS = SparseIntArray().apply {
            append(Surface.ROTATION_0, 270)
            append(Surface.ROTATION_90, 0)
            append(Surface.ROTATION_180, 90)
            append(Surface.ROTATION_270, 180)
        }
    }
}