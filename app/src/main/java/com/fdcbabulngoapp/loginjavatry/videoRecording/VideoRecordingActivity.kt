package com.fdcbabulngoapp.loginjavatry.videoRecording

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.fdcbabulngoapp.loginjavatry.ChallengeActivity
import com.fdcbabulngoapp.loginjavatry.MainActivity
import com.fdcbabulngoapp.loginjavatry.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.log


class VideoRecordingActivity : AppCompatActivity() {

    private lateinit var videoCapture: androidx.camera.video.VideoCapture<Recorder>
    private var recording: Recording? = null

    // Select back camera as a default
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var viewFinder: PreviewView
    private lateinit var videoCaptureBtn: Button
    private lateinit var flipCameraBtn: Button
    private lateinit var videoText: TextView
    private lateinit var allVideosBtn: Button
    private lateinit var homeBtn: ImageView
    private lateinit var logOutBtn: ImageView
    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var TAG = "VideoRecordingActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_recording)
        if (allPermissionsGranted()) {
            startCamera()
            Log.d(TAG, "Starting camera")
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        // Set up the listeners for video capture and flip camera buttons
        videoCaptureBtn = findViewById(R.id.video_capture_button)
        flipCameraBtn = findViewById(R.id.flip_camera_button)
        videoText = findViewById(R.id.textView)
        allVideosBtn = findViewById(R.id.allVideosBtn)
        homeBtn = findViewById(R.id.homeBtn)
        videoText.movementMethod = ScrollingMovementMethod()
        videoCaptureBtn.setOnClickListener { captureVideo() }
        flipCameraBtn.setOnClickListener { flipCamera() }
        allVideosBtn.setOnClickListener { displayAllVideos() }
        homeBtn.setOnClickListener {
            val intent = Intent(this@VideoRecordingActivity, ChallengeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        logOutBtn = findViewById(R.id.logoutBtn)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        logOutBtn.setOnClickListener {
            mAuth.signOut()
            val account: GoogleSignInAccount?= GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                mGoogleSignInClient.signOut().addOnSuccessListener {
                    //Toast.makeText(this, "Signing Out from google login", Toast.LENGTH_SHORT).show()
                    redirect2Login()
                }
            }
            redirect2Login()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun redirect2Login() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun flipCamera() {
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA)
            lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;
        else if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA)
            lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA;
        startCamera();
    }

    private fun captureVideo() {
        videoCapture = this.videoCapture

        videoCaptureBtn.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        //val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val name = "Video_" + UUID.randomUUID()
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/FDCApp")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@VideoRecordingActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        videoCaptureBtn.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                            setBackgroundColor(Color.RED)
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        videoCaptureBtn.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                            setBackgroundColor(Color.BLACK)
                        }
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)

                            //Opening the activity to preview recording
                            val intent = Intent(this@VideoRecordingActivity, PreviewVideoActivity::class.java)
                            intent.putExtra("videoUri", "${recordEvent.outputResults.outputUri}")
                            startActivity(intent)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                            videoCaptureBtn.apply {
                                text = getString(R.string.start_capture)
                                isEnabled = true
                            }
                        }
                    }
                }
            }
    }

    private fun displayAllVideos() {
        val intent = Intent(this@VideoRecordingActivity, VideoListActivity::class.java)
        startActivity(intent)
    }

    private fun startCamera() {
        Log.d(TAG, "Inside Start Camera")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            Log.d(TAG, "Starting Preview Build")
            viewFinder = findViewById(R.id.viewFinder)
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            Log.d(TAG, "Built Preview")

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = androidx.camera.video.VideoCapture.withOutput(recorder)
            Log.d(TAG, "Built Recorder")

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, lensFacing, preview, videoCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //Toast.makeText(this, "Starting camera", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}

