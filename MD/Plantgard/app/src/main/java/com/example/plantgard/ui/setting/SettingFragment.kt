package com.example.plantgard.ui.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.plantgard.databinding.FragmentSettingBinding
import com.example.plantgard.ui.login.LoginActivity

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingViewModel =
            ViewModelProvider(this)[SettingViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPref.getBoolean("dark_mode", false)

        // Set status awal switch
        binding.switchTheme.isChecked = isDarkModeEnabled
        applyTheme(isDarkModeEnabled)


        // Tambahkan listener untuk perubahan switch
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            saveThemePreference(isChecked)
            applyTheme(isChecked)
        }

        // Observe ViewModel
        settingViewModel.text.observe(viewLifecycleOwner) {
            binding.switchTheme.text = it
        }

        // Tambahkan klik untuk tombol logout


        binding.logoutButton.setOnClickListener {
            logout()
        }

        return root
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        // Simpan preferensi ke SharedPreferences
        sharedPref.edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        // Terapkan tema sesuai preferensi
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun logout() {
        // Hapus token dari SharedPreferences (pastikan menghapus data yang terkait dengan login)
        val sharedPref = activity?.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        sharedPref?.edit()?.clear()?.apply()  // Menghapus semua data yang ada di shared preferences

        // Tampilkan pesan logout berhasil
        Toast.makeText(context, "Berhasil logout!", Toast.LENGTH_SHORT).show()

        // Arahkan ke halaman login
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear tumpukan aktivitas
        startActivity(intent)

        // Tutup SettingFragment agar pengguna tidak dapat kembali ke halaman tersebut
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
