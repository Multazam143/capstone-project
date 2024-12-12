package com.example.plantgard.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.plantgard.R
import com.example.plantgard.ui.MainActivity
import com.example.plantgard.ui.login.LoginActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        // Delay selama 3 detik sebelum berpindah ke MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup WelcomeActivity agar tidak bisa kembali ke halaman ini
        }, 3000) // 3000 ms = 3 second


    }
}