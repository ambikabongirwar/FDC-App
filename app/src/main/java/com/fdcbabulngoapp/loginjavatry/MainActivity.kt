package com.fdcbabulngoapp.loginjavatry

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import com.fdcbabulngoapp.loginjavatry.R
import android.view.WindowManager
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.fdcbabulngoapp.loginjavatry.RegisterActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.fdcbabulngoapp.loginjavatry.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*

public var choosenlang=""
public var selectLanguageIndex = -1
open class MainActivity : AppCompatActivity() {
    lateinit var createnewAccount: TextView
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    lateinit var btnLogin: Button
    var emailPattern = "[a-z-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var forgotPwd: TextView
    open var progressDialog: ProgressDialog? = null
    lateinit var btnGoogle: ImageView
    open var mAuth: FirebaseAuth? = null
    open var mUser: FirebaseUser? = null

    lateinit var chooseLang:Button
    lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_main)
        chooseLang=findViewById(R.id.chooseLang)
        chooseLang.setOnClickListener { showChangeLanguageDialog() }

        createnewAccount = findViewById(R.id.CreateNewAccount)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        btnLogin = findViewById(R.id.btn_login)
        btnGoogle = findViewById(R.id.btnGoogle)
        forgotPwd = findViewById(R.id.forgotPassword)
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser


        forgotPwd.setOnClickListener {
            startActivity(Intent(this@MainActivity, ForgotPasswordActivity::class.java))
        }

        createnewAccount.setOnClickListener{
            intent = Intent(this@MainActivity, RegisterActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        btnLogin.setOnClickListener{ performLogin() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mUser = mAuth!!.currentUser
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        btnGoogle.setOnClickListener {
            signInGoogle()
        }
    }

    private fun setLocale(l: String?) {
        val locale = Locale(l)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("app_lang", l)
        editor.apply()
    }

    fun loadLocale() {
        val preferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val l = preferences.getString("app_lang", "")
        setLocale(l)
    }

    private fun showChangeLanguageDialog() {
        val lang = arrayOf("English", "తెలుగు", "हिंदी")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(lang, selectLanguageIndex) { dialog, which ->
            if (which == 0) {
                choosenlang=""
                selectLanguageIndex = 0
                setLocale("")
                recreate()
            } else if (which == 1) {
                choosenlang="తెలుగు"
                selectLanguageIndex = 1
                setLocale("te")
                recreate()
            } else if (which == 2) {
                selectLanguageIndex = 2
                choosenlang="हिंदी"
                setLocale("hi")
                recreate()
            }
        }
        mBuilder.create()
        mBuilder.show()
    }


    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        //Toast.makeText(this, " " + signInIntent, Toast.LENGTH_SHORT).show()
        //Log.d("MainActivity", "signInIntent: " + signInIntent)
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            //Toast.makeText(this, "Inside launcher", Toast.LENGTH_SHORT).show()
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account : GoogleSignInAccount?= task.result
            if(account != null) {
                //Toast.makeText(this, "handleResults", Toast.LENGTH_SHORT).show()
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener {
            if(it.isSuccessful) {
                val intent = Intent(this, ChallengeActivity::class.java)
                //Toast.makeText(this, "updateUI", Toast.LENGTH_SHORT).show()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin() {
        val email = inputEmail!!.text.toString()
        val password = inputPassword!!.text.toString()
        if (!email.matches(emailPattern.toRegex())) {
            inputEmail!!.error = "Enter correct email"
            inputEmail!!.requestFocus()
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword!!.error = "Enter proper password"
            inputPassword!!.requestFocus()
        } else {
            progressDialog!!.setMessage("Please wait while Login...")
            progressDialog!!.setTitle("Login")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog!!.dismiss()
                    sendUserToNextActivity()
                    Toast.makeText(this@MainActivity, "LOGIN Successfull", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    progressDialog!!.dismiss()

                    /*inputEmail.setError("User Not Registered!!!Please Register to login");
                                inputEmail.requestFocus();*/

                    //Toast.makeText(MainActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(
                        this@MainActivity,
                        "Please create account before LOGIN ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendUserToNextActivity() {
        val intent = Intent(this@MainActivity, ChallengeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK //this will stop back to come to this activity when user successfully registered
        startActivity(intent)
    }
}