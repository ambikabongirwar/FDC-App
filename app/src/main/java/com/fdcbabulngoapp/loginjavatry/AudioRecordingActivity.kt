package com.fdcbabulngoapp.loginjavatry

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.*

class AudioRecordingActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioRecordBtn: ImageView
    private lateinit var audioStopBtn: ImageView
    private lateinit var audioPlayBtn: ImageView
    private lateinit var textview: TextView
    private lateinit var shareButton: ImageView
    private lateinit var recordBtnTv: TextView
    private lateinit var playBtnTv: TextView
    private lateinit var stopBtnTv: TextView
    private lateinit var shareBtnTv: TextView
    private lateinit var logoutButton:ImageView
    private lateinit var homeBtn: ImageView
    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recording)


        audioRecordBtn = findViewById(R.id.audioRecordBtn)
        audioStopBtn= findViewById(R.id.audioStopBtn)
        audioPlayBtn= findViewById(R.id.audioPlayBtn)
        shareButton= findViewById(R.id.shareButton)
        recordBtnTv = findViewById(R.id.recordBtnTv)
        playBtnTv = findViewById(R.id.playBtnTv)
        stopBtnTv = findViewById(R.id.stopBtnTv)
        shareBtnTv = findViewById(R.id.shareBtnTv)
        logoutButton=findViewById(R.id.button)
        homeBtn = findViewById(R.id.homeBtn)

        mAuth = FirebaseAuth.getInstance()

        audioRecordBtn.visibility = View.GONE
        recordBtnTv.visibility = View.GONE

        audioStopBtn.visibility = View.GONE
        stopBtnTv.visibility = View.GONE

        audioPlayBtn.visibility = View.GONE
        playBtnTv.visibility = View.GONE

        shareButton.visibility = View.GONE
        shareBtnTv.visibility = View.GONE

        if (isMicrophonePresent) {
            microphonePermission
        }
        audioRecordBtn.visibility = View.VISIBLE
        recordBtnTv.visibility = View.VISIBLE
        textview=findViewById(R.id.text)
        textview.movementMethod = ScrollingMovementMethod()

        audioRecordBtn.setOnClickListener{btnRecordPressed()}
        audioStopBtn.setOnClickListener{btnStopPressed()}
        audioPlayBtn.setOnClickListener{btnPlayPressed()}
        shareButton.setOnClickListener { btnSharePressed() }
        homeBtn.setOnClickListener {
            val intent = Intent(this, ChallengeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        logoutButton.setOnClickListener{
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

    }

    private fun redirect2Login() {
        val intent = Intent(this@AudioRecordingActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun btnRecordPressed() {
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder!!.setOutputFile(recordingFilePath)
            mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            audioStopBtn.visibility= View.VISIBLE
            stopBtnTv.visibility= View.VISIBLE

            audioRecordBtn.visibility= View.GONE
            recordBtnTv.visibility = View.GONE

            audioPlayBtn.visibility= View.GONE
            playBtnTv.visibility= View.GONE

            shareButton.visibility= View.GONE
            shareBtnTv.visibility= View.GONE

            Toast.makeText(this, "Recording is started", Toast.LENGTH_SHORT).show()
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaRecorder.stop();
                    Toast.makeText(MainActivity.this,"Recording is stopped",Toast.LENGTH_SHORT).show();

                }
            }, 10000);

             */
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun btnStopPressed() {
        mediaRecorder!!.stop()
        mediaRecorder!!.release()
        mediaRecorder = null

        audioRecordBtn.visibility= View.VISIBLE
        recordBtnTv.visibility = View.VISIBLE

        audioStopBtn.visibility= View.GONE
        stopBtnTv.visibility= View.GONE

        audioPlayBtn.visibility= View.VISIBLE
        playBtnTv.visibility= View.VISIBLE

        shareButton.visibility= View.VISIBLE
        shareBtnTv.visibility= View.VISIBLE

        Toast.makeText(this, "Recording is stopped", Toast.LENGTH_SHORT).show()
    }

    fun btnPlayPressed() {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(recordingFilePath)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            Toast.makeText(this, "Recording is playing", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun btnSharePressed() {
        try{

            shareButton.setOnClickListener {
                /*val uri: Uri = Uri.parse(recordingFilePath)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra("content_url", "#FunDoChallenge #Babul")
                shareIntent.putExtra(Intent.EXTRA_TEXT, "#FunDoChallenge")
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "Share via"))*/
                val authorities = "com.fdcbabulngoapp.loginjavatry.fileprovider"

                val path = FileProvider.getUriForFile(this, authorities, recordedFile)

                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, path)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.type = "*/*"
                shareIntent.putExtra("content_url", "#FunDuChallenge #BabulNGO$choosenlang")
                shareIntent.putExtra(Intent.EXTRA_TEXT, "#FunDuChallenge #BabulNGO$choosenlang")
                startActivity(Intent.createChooser(shareIntent, "Share..."))



            }
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }



    private val isMicrophonePresent: Boolean
        get() = this.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    private val microphonePermission: Unit
        get() {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    MICROPHONE_PERMISSION_CODE
                )


            }
        }
    private val recordingFilePath: String
        get() {
            val contextWrapper = ContextWrapper(applicationContext)
            val musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            val file = File(musicDirectory, "BabulFDCAudio" + ".mp3")
            return file.path
        }
    private val recordedFile: File
        get() {
            val contextWrapper = ContextWrapper(applicationContext)
            val musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            return File(musicDirectory, "BabulFDCAudio" + ".mp3")
        }

    companion object {
        private const val MICROPHONE_PERMISSION_CODE = 200


    }
}