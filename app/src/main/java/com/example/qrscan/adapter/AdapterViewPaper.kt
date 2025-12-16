package com.example.qrscan.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.qrscan.MainActivity
import com.example.qrscan.view.CreateDetailFragment
import com.example.qrscan.view.CreateOptionFragment
import com.example.qrscan.view.GenerateFragment
import com.example.qrscan.view.HistoryFragment
import com.example.qrscan.view.OnBoardFragment1
import com.example.qrscan.view.OnBoardFragment2
import com.example.qrscan.view.OnBoardFragment3
import com.example.qrscan.view.ScanFragment
import com.example.qrscan.view.SettingFragment
import com.example.qrscan.view.SplashFragment

class AdapterViewPaper(mainActivity: MainActivity) : FragmentStateAdapter(mainActivity){
    override fun createFragment(position: Int): Fragment {
       return when(position){
           0-> SplashFragment()
           1-> OnBoardFragment1()
           2-> OnBoardFragment2()
           3-> OnBoardFragment3()
           5-> HistoryFragment()
           4-> ScanFragment()
           6-> GenerateFragment()
           7-> SettingFragment()
           8-> CreateOptionFragment()
           9-> CreateDetailFragment()

           else -> SplashFragment()
       }

    }

    override fun getItemCount(): Int =10



}

