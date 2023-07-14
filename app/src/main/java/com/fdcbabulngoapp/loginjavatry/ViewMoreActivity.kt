package com.fdcbabulngoapp.loginjavatry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ViewMoreActivity : AppCompatActivity() {

    private lateinit var homeButton: ImageView
    private lateinit var logOutButton: ImageView

    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_know_more)

        homeButton = findViewById(R.id.homeBtn)
        logOutButton = findViewById(R.id.logoutBtn1)
        mAuth = FirebaseAuth.getInstance()

        logOutButton.visibility = View.GONE

        homeButton.setOnClickListener {
            val intent = Intent(this@ViewMoreActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account: GoogleSignInAccount?= GoogleSignIn.getLastSignedInAccount(this)

        if (mAuth.currentUser != null || account != null) {
            logOutButton.visibility = View.VISIBLE
        }
        logOutButton.setOnClickListener {
            mAuth.signOut()
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
        val intent = Intent(this@ViewMoreActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}
