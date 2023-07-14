package com.fdcbabulngoapp.loginjavatry

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {

    private lateinit var goToCp: Button
    private lateinit var viewMore:Button
    private lateinit var basicInst:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        basicInst=findViewById(R.id.BasicInstru)
        basicInst.movementMethod = ScrollingMovementMethod()
        goToCp=findViewById(R.id.GoToCP)





        viewMore=findViewById(R.id.viewMore)

        goToCp.setOnClickListener {
            val intent = Intent(this@HomeActivity, ChallengeActivity::class.java)

            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        viewMore.setOnClickListener {
            val intent=Intent(this@HomeActivity,ViewMoreActivity::class.java)
            startActivity(intent)
        }

    }


}