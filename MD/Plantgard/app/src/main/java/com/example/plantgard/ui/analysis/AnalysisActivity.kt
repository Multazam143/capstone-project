package com.example.plantgard.ui.analysis

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plantgard.R
import com.example.plantgard.api.ApiConfig
import com.example.plantgard.databinding.ActivityAnalysisBinding
import com.example.plantgard.network.PredictionResponse
import com.example.plantgard.getImageUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AnalysisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalysisBinding
    private var currentImageUri: Uri? = null
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar // Pastikan Anda punya progress bar di layout

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data dari Intent
        val plantType = intent.getStringExtra("PLANT_TYPE") ?: "Tanaman tidak diketahui"
        val plantTextView: TextView = findViewById(R.id.TitleTextView)
        plantTextView.text = "Analisis $plantType"

        setupListener()
        binding.upload.setOnClickListener {
            if (currentImageUri != null) {
                // Ambil token dari SharedPreferences
                val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
                val token = sharedPref.getString("auth_token", null)

                if (token != null) {
                    uploadImageToAPI(plantType, token)
                } else {
                    showToast("Token tidak ditemukan. Silakan login terlebih dahulu.")
                }
            } else {
                showToast("Harap pilih atau ambil gambar terlebih dahulu")
            }
        }
    }

    private fun setupListener() {
        // Tombol untuk membuka kamera
        binding.cameraButton.setOnClickListener {
            if (checkAndRequestPermissions()) {
                currentImageUri = getImageUri(this)
                cameraLaunch.launch(currentImageUri!!)
            }
        }

        binding.galleryButton.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private val cameraLaunch =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                showImage()
            } else {
                currentImageUri = null
                showToast("Gagal mengambil gambar")
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("Tidak ada gambar yang dipilih")
        }
    }

    private fun showImage() {
        val uri = currentImageUri
        if (uri != null) {
            Log.d("Image URI", "showImage: $uri")
            binding.previewImageView.setImageURI(uri) // Menampilkan gambar
        } else {
            Log.d("Image URI", "Tidak ditemukan URI gambar")
        }
    }

    private fun convertToJpeg(uri: Uri, context: Context): File? {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }

            val file = File(context.cacheDir, "converted_image.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun uploadImageToAPI(plantType: String, token: String) {
        val filePath = getFilePathFromUri(currentImageUri!!)
        if (filePath != null) {
            val file = convertToJpeg(currentImageUri!!, this)
            if (file != null) {
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestBody)

                val headers = mapOf("Authorization" to "Bearer $token")

                Log.d("UploadImage", "Headers: $headers")
                Log.d("UploadImage", "File: ${file.name}, File Path: ${file.absolutePath}")

                progressBar.visibility = ProgressBar.VISIBLE

                val call = ApiConfig.apiService.uploadImage(plantType, headers, body)
                call.enqueue(object : Callback<PredictionResponse> {
                    override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                        progressBar.visibility = ProgressBar.GONE
                        Log.d("UploadImage", "Response code: ${response.code()}")
                        Log.d("UploadImage", "Response message: ${response.message()}")

                        if (response.isSuccessful) {
                            val predictionResponse = response.body()
                            Log.d("UploadImage", "Prediction response: $predictionResponse")

                            if (predictionResponse != null) {
                                val intent = Intent(this@AnalysisActivity, ResultActivity::class.java)
                                intent.putExtra("PLANT_TYPE", plantType)
                                intent.putExtra("imageUri", currentImageUri.toString())  // Kirim URI gambar

                                // Kirim data penyakit dari API
                                val disease = predictionResponse.data.disease
                                intent.putExtra("DISEASE_TYPE", disease.type)
                                intent.putExtra("DISEASE_DESCRIPTION", disease.description)
                                intent.putExtra("DISEASE_TREATMENT", disease.treatment)
                                intent.putExtra("DISEASE_PREVENTION", disease.prevention)

                                startActivity(intent)
                            } else {
                                Log.e("UploadImage", "Invalid data from API")
                                showToast("Data tidak valid dari API")
                            }
                        } else {
                            // Log error message if the response is not successful
                            Log.e("UploadImage", "Failed to get data: ${response.errorBody()?.string()}")
                            showToast("Gagal mengambil data:  ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                        progressBar.visibility = ProgressBar.GONE
                        // Log failure message in case of network issues
                        Log.e("UploadImage", "Network error: ${t.message}", t)
                        showToast("Kesalahan jaringan: ${t.message}")
                    }
                })
            } else {
                Log.e("UploadImage", "File conversion failed")
                showToast("Konversi file gagal")
            }
        } else {
            Log.e("UploadImage", "File path is null")
            showToast("File path is null")
        }
    }


    private fun getFilePathFromUri(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndex("_data")
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    // Animasi loading
    private fun showLoading(isLoading: Boolean) {
        val animation = if (isLoading) {
            AlphaAnimation(0f, 1f).apply { duration = 300 }
        } else {
            AlphaAnimation(1f, 0f).apply { duration = 300 }
        }
        binding.progressBar.startAnimation(animation)
        binding.progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Mengecek dan meminta izin kamera
    private fun checkAndRequestPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            false
        } else {
            true
        }
    }

    companion object {
        private const val TAG = "AnalysisActivity"
        private const val CAMERA_PERMISSION_CODE = 100
    }
}
