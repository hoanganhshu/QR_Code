package com.example.qrscan

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.adapter.AdapterViewPaper
import com.example.qrscan.view.GenerateFragment
import com.example.qrscan.view.HistoryFragment
import com.example.qrscan.view.ScanFragment
import com.example.qrscan.view.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavController {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private val navStack = ArrayDeque<Int>()
    private var isHandlingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottomNav)

        viewPager.adapter = AdapterViewPaper(this)


        onBackPressedDispatcher.addCallback(this) {
            if (navStack.isNotEmpty()) {
                isHandlingBack = true
                viewPager.setCurrentItem(navStack.removeLast(), false)
            } else {
                finish()
            }
        }
        bottomNav.visibility = View.GONE


        bottomNav.setOnItemSelectedListener { item ->
            navStack.clear()
            when (item.itemId) {
                R.id.nav_scan -> navigateMain(ScanFragment())
                R.id.nav_settings -> navigateMain(SettingFragment())
                R.id.nav_history -> navigateMain(HistoryFragment())
                R.id.nav_qr_code -> navigateMain(GenerateFragment())
            }
            true
        }



    }
    fun goToMainFlow() {

        viewPager.visibility = View.GONE


        findViewById<View>(R.id.mainContainer).visibility = View.VISIBLE


        bottomNav.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, ScanFragment())
            .commit()
    }



    fun showBottomNav(show: Boolean) {
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun requestBottomNav(show: Boolean) {
        showBottomNav(show)
    }
    fun navigateMain(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}
interface BottomNavController {
    fun requestBottomNav(show: Boolean)
}


