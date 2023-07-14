package com.fdcbabulngoapp.loginjavatry

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.fdcbabulngoapp.loginjavatry.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    var inputEmail: EditText? = null
    var emailPattern = "[a-z-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    lateinit var resetPwdBtn: Button
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputEmail = findViewById(R.id.inputEmailFp)
        resetPwdBtn = findViewById(R.id.resetPwBtn)
        mAuth = FirebaseAuth.getInstance()

        resetPwdBtn.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = inputEmail!!.text.toString()
        if (!email.matches(emailPattern.toRegex())) {
            inputEmail!!.error = "Enter correct email"
            inputEmail!!.requestFocus()
        } else {
            mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Please check your email", Toast.LENGTH_SHORT).show()
                    val intent= Intent(this,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .addOnFailureListener{ it: Exception ->
                    if (it.toString().contains("FirebaseAuthInvalidUserException")) {
                        Toast.makeText(
                            this,
                            "User doesn't exist. Please create an account.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent= Intent(this,RegisterActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    else
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

}