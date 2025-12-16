package com.example.qrscan.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.qrscan.BaseFragment
import com.example.qrscan.MainActivity
import com.example.qrscan.R
import com.example.qrscan.databinding.FragmentOnBoard2Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardFragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardFragment2 : BaseFragment<FragmentOnBoard2Binding>(){
    override fun inflate(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoard2Binding {
        return FragmentOnBoard2Binding.inflate(layoutInflater,container,false)
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
        (activity as? MainActivity)?.showBottomNav(false)
    }

}

