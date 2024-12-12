package com.example.plantgard.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.plantgard.api.ApiConfig
import com.example.plantgard.databinding.ActivityRegisterBinding
import com.example.plantgard.ui.login.LoginActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol untuk pindah ke login
        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Tombol untuk register
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditTextLayout.editText?.text.toString().trim()
            val email = binding.emailEditTextLayout.editText?.text.toString().trim()
            val password = binding.passwordEditTextLayout.editText?.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        playAnimation()
    }

    private fun registerUser(name: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Membuat objek RegisterRequest
                val request = RegisterRequest(name, email, password)

                // Mengirim permintaan ke API
                ApiConfig.apiService.register(request)

                // Jika berhasil
                Toast.makeText(this@RegisterActivity, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            } catch (e: Exception) {
                // Jika gagal
                Toast.makeText(this@RegisterActivity, "Gagal mendaftar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.registerTitleTextView, View.ALPHA, 1f).setDuration(100)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val inputName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val inputEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val inputPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.loginTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                name,
                inputName,
                title,
                email,
                inputEmail,
                password,
                inputPassword,
                login,
                register
            )
            startDelay = 100
        }.start()
    }
}
