package com.example.qrscan.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.BottomNavController
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentOnBoard1Binding


class OnBoardFragment1 : BaseFragment<FragmentOnBoard1Binding>() {


    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoard1Binding {
       return FragmentOnBoard1Binding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mBinding.next.setOnClickListener {
            val next=requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            next.currentItem+=1
        }
        mBinding.skip.setOnClickListener {

        }
    }
    override fun onResume() {
        super.onResume()
        (activity as? BottomNavController)?.requestBottomNav(false)
    }



}

