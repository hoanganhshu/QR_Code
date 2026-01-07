package com.example.qrscan.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.qrscan.MainActivity
import com.example.qrscan.view.OnBoardFragment1
import com.example.qrscan.view.OnBoardFragment2
import com.example.qrscan.view.OnBoardFragment3
import com.example.qrscan.view.SplashFragment

class AdapterViewPaper(mainActivity: MainActivity) : FragmentStateAdapter(mainActivity){
    override fun createFragment(position: Int): Fragment {
       return when(position){
           0-> SplashFragment()
           1-> OnBoardFragment1()
           2-> OnBoardFragment2()
           3-> OnBoardFragment3()
//           5-> HistoryFragment()
//           4-> ScanFragment()
//           6-> GenerateFragment()
//           7-> SettingFragment()
//           8-> CreateOptionFragment()
//           9-> CreateDetailFragment()
//           10-> ResultFragment()
//           11-> HistoryScanFragment()
//           12-> PrivacyFragment()

           else -> throw IllegalStateException("Invalid ViewPager position: $position")

       }

    }

    override fun getItemCount(): Int =4
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in 0L..12L
    }


}

