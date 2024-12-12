package com.example.plantgard.ui.analysis

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.plantgard.R
import com.example.plantgard.api.ApiConfig
import com.example.plantgard.databinding.ActivityResultBinding
import com.example.plantgard.network.PredictionResponse
import com.example.plantgard.ui.setting.SettingViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: SettingViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var previewImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        previewImageView = binding.previewImageView

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        // Retrieve data from the Intent
        val plantType = intent.getStringExtra("PLANT_TYPE") ?: "default"  // Default if not available
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        // Check if the token exists
        if (token == null) {
            Toast.makeText(this, "Token tidak ditemukan. Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    private fun displayResults(predictionResponse: PredictionResponse) {
        binding.gejala.text = "Gejala: ${predictionResponse.data.disease.type}"
        binding.penanganan.text = "Saran Penanganan: ${predictionResponse.data.disease.treatment}"

        // Load disease image using Picasso library
//        val imageUrl = predictionResponse.data?.disease?.image
//        Picasso.get()
//            .load(imageUrl)
//            .error(R.drawable.ic_image_24)
//            .into(binding.previewImageView)
    }
}
