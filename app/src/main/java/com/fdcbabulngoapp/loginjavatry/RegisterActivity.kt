package com.fdcbabulngoapp.loginjavatry

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.EditText
import android.app.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import com.fdcbabulngoapp.loginjavatry.R
import android.view.WindowManager
import android.content.Intent
import android.view.View
import android.widget.Button
import com.fdcbabulngoapp.loginjavatry.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import android.widget.Toast
import com.fdcbabulngoapp.loginjavatry.HomeActivity

class RegisterActivity : AppCompatActivity() {
    lateinit var alreadyHaveAccount: TextView
    var inputEmail: EditText? = null
    var inputPassword: EditText? = null
    var inputConfirmPassword: EditText? = null
    lateinit var btnRegister: Button
    var emailPattern = "[a-z-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var progressDialog: ProgressDialog? = null
    var mAuth: FirebaseAuth? = null
    var mUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.confirmPassword)
        btnRegister = findViewById(R.id.btn_register)
        progressDialog = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        alreadyHaveAccount.setOnClickListener(View.OnClickListener {
            intent = Intent(this@RegisterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })
        btnRegister.setOnClickListener(View.OnClickListener { PerformAuth() })
    }

    private fun PerformAuth() {
        val email = inputEmail!!.text.toString()
        val password = inputPassword!!.text.toString()
        val confirmPassword = inputConfirmPassword!!.text.toString()
        if (!email.matches(emailPattern.toRegex())) {
            inputEmail!!.error = "Enter correct email"
            inputEmail!!.requestFocus()
        } else if (password.isEmpty() || password.length < 6) {
            inputPassword!!.error = "Enter proper password"
        } else if (password != confirmPassword) {
            inputConfirmPassword!!.error = "Password not matched"
        } else {
            progressDialog!!.setMessage("Please wait while Registration...")
            progressDialog!!.setTitle("Registration")
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog!!.dismiss()
                    sendUserToNextActivity()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration Successfull",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    progressDialog!!.dismiss()
                    //Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_LONG).show();
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email already registered! Please LOGIN",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun sendUserToNextActivity() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK //this will stop back to come to this activity when user successfully registered
        startActivity(intent)
    }
}