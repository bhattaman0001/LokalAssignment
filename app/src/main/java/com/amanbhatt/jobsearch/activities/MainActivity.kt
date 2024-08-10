package com.amanbhatt.jobsearch.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.amanbhatt.jobsearch.R
import com.amanbhatt.jobsearch.databinding.ActivityMainBinding
import com.amanbhatt.jobsearch.fragments.BookmarkFragment
import com.amanbhatt.jobsearch.fragments.JobFragment
import com.amanbhatt.jobsearch.model.NetworkUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavView) { v: View, insets: WindowInsetsCompat? ->
            val insetsCompat = insets ?: WindowInsetsCompat.Builder().build()
            val bottomInset = insetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(0, 0, 0, bottomInset)
            insetsCompat
        }

        if (savedInstanceState == null) {
            currentFragment = JobFragment()
            loadFragment(currentFragment)
        }

        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            val itemId = menuItem.itemId
            handleNavigation(itemId)
            true
        }

        binding.bottomNavView.selectedItemId = R.id.jobs_nav_btn
    }

    private fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment!!)
            .commit()
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please turn on your internet connection to continue.")
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNegativeButton("Cancel") { _, _ ->
                loadFragment(BookmarkFragment())
            }
            .show()
    }

    private fun handleNavigation(itemId: Int) {
        var selectedFragment: Fragment? = null
        if (itemId == R.id.jobs_nav_btn) {
            if (NetworkUtils.isConnectedToInternet(this)) {
                selectedFragment = JobFragment()
            } else {
                showNoInternetDialog()
                return
            }
        } else if (itemId == R.id.bookmark_nav_btn) {
            selectedFragment = BookmarkFragment()
        }

        if (selectedFragment != null && (currentFragment == null || selectedFragment.javaClass != currentFragment!!.javaClass)) {
            currentFragment = selectedFragment
            loadFragment(currentFragment)
        }
    }
}
