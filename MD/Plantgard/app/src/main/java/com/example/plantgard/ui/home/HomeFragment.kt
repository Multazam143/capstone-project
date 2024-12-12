package com.example.plantgard.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.plantgard.databinding.FragmentHomeBinding
import com.example.plantgard.ui.analysis.AnalysisActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi ViewModel
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Binding layout
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observasi data dari ViewModel
        homeViewModel.text.observe(viewLifecycleOwner) { text ->
            binding.plantgardtext.text = text
        }

        // Tambahan: Set OnClickListener untuk CardView
        binding.padi.setOnClickListener {
            navigateToAnalysisActivity("rice")
        }

        binding.jagung.setOnClickListener {
            navigateToAnalysisActivity("corn")
        }

        binding.cabe.setOnClickListener {
            navigateToAnalysisActivity("chili")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Hindari kebocoran memori
    }

    // Fungsi tambahan untuk melakukan Intent
    private fun navigateToAnalysisActivity(plantType: String) {
        val intent = Intent(requireContext(), AnalysisActivity::class.java)
        intent.putExtra("PLANT_TYPE", plantType) // Kirim data ke AnalysisActivity
        startActivity(intent)
    }
}
