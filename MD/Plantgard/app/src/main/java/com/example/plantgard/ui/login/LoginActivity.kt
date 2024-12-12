package com.example.plantgard.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.plantgard.api.ApiConfig
import com.example.plantgard.databinding.ActivityLoginBinding
import com.example.plantgard.ui.MainActivity
import com.example.plantgard.ui.register.RegisterActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditTextLayout.editText?.text.toString().trim()
            val password = binding.passwordEditTextLayout.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        playAnimation()
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Membuat objek LoginRequest
                val request = LoginRequest(email, password)

                // Mengirim permintaan ke API
                val response = ApiConfig.apiService.login(request)

                // Memastikan data dan token tidak null
                val token = response.data?.token
                if (token != null) {
                    // Jika login berhasil
                    Toast.makeText(this@LoginActivity, "Login berhasil: ${response.message}", Toast.LENGTH_SHORT).show()

                    // Menyimpan token ke SharedPreferences
                    saveToken(token)

                    // Pindah ke halaman utama
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Jika respons tidak sesuai
                    Toast.makeText(this@LoginActivity, "Login gagal: Token tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                // Jika terjadi kesalahan pada HTTP
                Toast.makeText(this@LoginActivity, "Gagal login: ${e.message()}", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                // Jika terjadi kesalahan jaringan
                Toast.makeText(this@LoginActivity, "Gagal login: Kesalahan jaringan.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Jika terjadi kesalahan umum
                Toast.makeText(this@LoginActivity, "Gagal login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToken(token: String) {
        // Mengambil SharedPreferences
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Menyimpan token
        editor.putString("auth_token", token)
        editor.apply()
        android.util.Log.d("LoginActivity", "Token disimpan: $token")
    }



    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.loginTitleTextView, View.ALPHA, 1f).setDuration(100)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val inputEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val inputPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val register = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
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
