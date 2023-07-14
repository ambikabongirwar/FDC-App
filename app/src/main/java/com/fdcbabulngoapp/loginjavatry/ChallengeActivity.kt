package com.fdcbabulngoapp.loginjavatry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import com.fdcbabulngoapp.loginjavatry.databinding.ActivityChallengeBinding
import com.fdcbabulngoapp.loginjavatry.videoRecording.VideoRecordingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ChallengeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityChallengeBinding
    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user == null) {
            redirect2Login()
        }



       /* binding.scrollHeading.isSelected = true;
        binding.BasicInstru.movementMethod = ScrollingMovementMethod();*/

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.button.setOnClickListener{
            mAuth.signOut()
            val account: GoogleSignInAccount ?= GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                mGoogleSignInClient.signOut().addOnSuccessListener {
                    //Toast.makeText(this, "Signing Out from google login", Toast.LENGTH_SHORT).show()
                    redirect2Login()
                }
            }
            redirect2Login()
        }
        binding.videoBtn.setOnClickListener{
            redirect2VideoRecording()
        }

        binding.audioBtn.setOnClickListener {
            redirect2AudioRecording()
        }

    }

//    override fun onBackPressed() {
//
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//        finish()
//        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    }

    private fun redirect2AudioRecording() {
        val intent = Intent(this@ChallengeActivity, AudioRecordingActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun redirect2Login() {
        val intent = Intent(this@ChallengeActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun redirect2VideoRecording() {
        val intent = Intent(this@ChallengeActivity, VideoRecordingActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)


    }
}