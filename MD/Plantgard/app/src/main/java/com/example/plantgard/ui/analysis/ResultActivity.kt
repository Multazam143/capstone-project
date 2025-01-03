package com.example.plantgard.ui.analysis

import android.content.Context
import android.net.Uri
import android.os.Bundle
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
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

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

        // Retrieve image URI from the Intent
        val imageUri = intent.getStringExtra("imageUri")
        if (!imageUri.isNullOrEmpty()) {
            uploadImage(imageUri, token, plantType)
        } else {
            Toast.makeText(this, "Gambar tidak ditemukan.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to upload the image to the API
    private fun uploadImage(imageUri: String, token: String, plantType: String) {
        val uri = Uri.parse(imageUri)
        val filePath = getFilePathFromUri(this, uri)

        // Validate if file path is correct
        if (filePath.isNullOrEmpty()) {
            Toast.makeText(this, "Gagal mendapatkan path gambar.", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(filePath)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val headers = mapOf("Authorization" to "Bearer $token")

        // Show progress bar while waiting for the response
        progressBar.visibility = View.VISIBLE
        val call = ApiConfig.apiService.uploadImage(plantType, headers, body)
        call.enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val predictionResponse = response.body()
                    if (predictionResponse?.data?.disease != null) {
                        displayResults(predictionResponse)
                    } else {
                        Toast.makeText(this@ResultActivity, "Data tidak valid dari API", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Log error message if the response is not successful
                    Toast.makeText(this@ResultActivity, "Gagal mengambil data: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                // Log failure message in case of network issues
                Toast.makeText(this@ResultActivity, "Kesalahan jaringan: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Function to get file path from URI
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

    // Function to display the results from the prediction response
    private fun displayResults(predictionResponse: PredictionResponse) {
        binding.gejala.text = "Gejala: ${predictionResponse.data?.disease?.type ?: "Tidak diketahui"}"
        binding.penanganan.text = "Saran Penanganan: ${predictionResponse.data?.disease?.treatment ?: "Tidak tersedia"}"

        // Load disease image using Picasso library
        val imageUrl = predictionResponse.data?.disease?.image
        Picasso.get()
            .load(imageUrl)
            .error(R.drawable.ic_image_24)
            .into(binding.previewImageView)
    }
}
