package com.fdcbabulngoapp.loginjavatry.videoRecording

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fdcbabulngoapp.loginjavatry.ChallengeActivity
import com.fdcbabulngoapp.loginjavatry.MainActivity
import com.fdcbabulngoapp.loginjavatry.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class VideoListActivity: AppCompatActivity() {
    private lateinit var videoListAdapter: VideoListAdapter
    private lateinit var homeBtn: ImageView
    private lateinit var logoutBtn: ImageView
    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videos_list)

        // Get the list of recorded videos
        val recordedVideos: MutableList<RecordedVideoModel> = getRecordedVideos()

        val recyclerview = findViewById<RecyclerView>(R.id.videosRV)

        recyclerview.layoutManager = LinearLayoutManager(this)
        videoListAdapter = VideoListAdapter(this, applicationContext, recordedVideos)
        homeBtn = findViewById(R.id.homeBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        mAuth = FirebaseAuth.getInstance()

        recyclerview.adapter = videoListAdapter

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

    }

    private fun getRecordedVideos(): MutableList<RecordedVideoModel> {
        val recordedVideos = mutableListOf<RecordedVideoModel>()

        val contentResolver: ContentResolver = applicationContext.contentResolver
        val videoUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        val cursor: Cursor? = contentResolver.query(
            videoUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use { c ->
            val dataIndex = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val nameIndex = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)

            while (c.moveToNext()) {
                val videoPath = c.getString(dataIndex)
                val videoName = c.getString(nameIndex)
                val videoUri = Uri.parse(videoPath)
                recordedVideos.add(RecordedVideoModel(videoName, videoUri.toString()))
            }
        }

        return recordedVideos
    }

    private fun redirect2Login() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}