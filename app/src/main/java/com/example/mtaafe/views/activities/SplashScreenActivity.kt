package com.example.mtaafe.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mtaafe.R
import com.example.mtaafe.utils.SessionManager
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        sessionManager = SessionManager(this)
        context = this

        Timer().schedule(1500) {
            if(sessionManager.isUserLoggedIn()) {
                val intent = Intent(context, QuestionsListActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}