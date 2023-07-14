package com.fdcbabulngoapp.loginjavatry.videoRecording

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fdcbabulngoapp.loginjavatry.ChallengeActivity
import com.fdcbabulngoapp.loginjavatry.MainActivity
import com.fdcbabulngoapp.loginjavatry.R
import com.fdcbabulngoapp.loginjavatry.choosenlang
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class PreviewVideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var shareBtn: Button
    private lateinit var historyBtn: Button
    private lateinit var logoutBtn:ImageView
    private lateinit var mAuth:FirebaseAuth
    private lateinit var deleteBtn: Button
    private lateinit var homeBtn: ImageView
    lateinit var mGoogleSignInClient: GoogleSignInClient

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_video)

        videoView = findViewById(R.id.videoView)

        val videoUri = intent.getStringExtra("videoUri")
        Toast.makeText(this, "" + videoUri, Toast.LENGTH_SHORT)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        val uri = Uri.parse(videoUri)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()

        shareBtn = findViewById(R.id.share_button)
        shareBtn.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "video/*"
            sendIntent.putExtra("content_url", "#FunDuChallenge #BabulNGO$choosenlang")
            sendIntent.putExtra(Intent.EXTRA_TEXT, "#FunDuChallenge #BabulNGO$choosenlang")
            sendIntent.putExtra(
                Intent.EXTRA_STREAM,
                uri
            )
            startActivity(Intent.createChooser(sendIntent, "Share to:"))
        }

        homeBtn = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this, ChallengeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        historyBtn = findViewById(R.id.history_button)
        historyBtn.setOnClickListener {
            val intent = Intent(this, VideoListActivity::class.java)
            startActivity(intent)
        }

        logoutBtn = findViewById(R.id.logoutBtn)
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        logoutBtn.setOnClickListener {
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

        deleteBtn = findViewById(R.id.delete_button)
        deleteBtn.setOnClickListener{

            deleteVideo(this, applicationContext, uri)
        }
    }

    private fun redirect2Login() {
        val intent = Intent(this@PreviewVideoActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}

fun deleteVideo(context: Context, applicationContext: Context, uri: Uri) {
    val resolver = applicationContext.contentResolver
    val selection = null
    val selectionArgs = null

    // Perform the actual removal.
    val numVideosRemoved = resolver.delete(
        uri,
        selection,
        selectionArgs)
    if (numVideosRemoved == 1) {
        Toast.makeText(context, "Video Successfully Deleted", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, VideoRecordingActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK //this will stop back to come to this activity when video is deleted
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Please enable permissions for the app", Toast.LENGTH_SHORT).show()
    }
}
