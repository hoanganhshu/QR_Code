package com.example.qrscan

import com.example.qrscan.adapter.AdapterViewPaper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.view.ScanFragment
import com.example.qrscan.view.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPaper = findViewById<ViewPager2>(R.id.viewPager)
        viewPaper.adapter = AdapterViewPaper(this)


        val bottom_nav=findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom_nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_scan ->{
                   viewPaper.currentItem=4
                    true
                }
                R.id.nav_settings ->{
                   viewPaper.currentItem=7


                    true
                }
                R.id.nav_history ->{
                    viewPaper.currentItem=5

                    true
                }
                R.id.nav_qr_code ->{
                    viewPaper.currentItem=6

                    true
                }
                else -> false

            }
        }



    }


    fun showBottomNav(show: Boolean) {
        val bottomNav = findViewById<View>(R.id.bottomNav)
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
    }


}