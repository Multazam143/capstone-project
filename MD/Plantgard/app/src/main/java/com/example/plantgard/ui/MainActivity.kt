package com.example.plantgard.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.plantgard.R
import com.example.plantgard.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and bind itf
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Initialize BottomNavigationView
        val navView: BottomNavigationView = binding.navView

        // Initialize NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Define top-level destinations (no back button for these)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_setting
            )
        )

        // Set the toolbar as ActionBar
        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Set up ActionBar with NavController and AppBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Link BottomNavigationView with NavController
        navView.setupWithNavController(navController)
    }

    // Inflate the AppBar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar, menu)
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                // Navigate to About Activity
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Handle navigation up (for the back button in ActionBar)
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
